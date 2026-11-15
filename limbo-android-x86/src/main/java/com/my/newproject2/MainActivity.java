package com.my.newproject2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.limbo.emu.main.LimboEmuActivity;
import com.limbo.emu.main.databinding.RuscHomeBinding;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Entrada do RUSC Virtual, adaptada do projeto exportado do Sketchware Pro.
 * A VM real e gerenciada pelo modulo Limbo; esta tela abre seu gerenciador.
 */
public class MainActivity extends Activity {
    private RuscHomeBinding binding;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        binding = RuscHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textview3.setText("Motor integrado ao aplicativo.\n" +
                "Proximo passo: configurar um disco e a imagem Android na tela da VM.");

        binding.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    prepararArmazenamento();
                    startActivity(new Intent(MainActivity.this, LimboEmuActivity.class));
                } catch (Throwable error) {
                    binding.textview3.setText(
                        "Nao foi possivel abrir a maquina virtual:\n" + error.toString());
                }
            }
        });

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarMotor();
            }
        });
    }

    private void prepararArmazenamento() throws IOException {
        File base = getExternalFilesDir(null);
        if (base == null) base = getFilesDir();

        File vm = new File(base, "RUSC_Virtual/VM1");
        String[] subpastas = {"disks", "system", "logs", "shared"};
        for (String subpasta : subpastas) {
            File pasta = new File(vm, subpasta);
            if (!pasta.isDirectory() && !pasta.mkdirs()) {
                throw new IOException("Falha ao criar " + subpasta);
            }
        }

        File arquivo = new File(vm, "vm.properties");
        if (!arquivo.exists()) {
            Properties props = new Properties();
            props.setProperty("name", "RUSC Android");
            props.setProperty("version", "1");
            props.setProperty("host_arch", "auto");
            props.setProperty("guest_arch", "x86_64");
            props.setProperty("state", "needs_image");
            FileOutputStream out = new FileOutputStream(arquivo);
            try {
                props.store(out, "RUSC Virtual / Sketchware");
            } finally {
                out.close();
            }
        }
    }

    private void verificarMotor() {
        File dir = new File(getApplicationInfo().nativeLibraryDir);
        String[] motores = {"qemu-system-x86_64", "qemu-system-aarch64"};
        StringBuilder resultado = new StringBuilder("RUSC VIRTUAL\n\n");

        for (String motor : motores) {
            boolean presente = new File(dir, "lib" + motor + ".so").isFile();
            resultado.append(motor).append(": ")
                     .append(presente ? "encontrado" : "nao incluido")
                     .append("\n");
        }

        resultado.append("\nEste teste nao inicia o Android.");
        binding.textview3.setText(resultado.toString());
    }
}
