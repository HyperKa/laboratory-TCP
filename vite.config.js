import { defineConfig } from "vite";
import react from '@vitejs/plugin-react';

export default defineConfig({
    plugins: [react()],
    build: {
        outDir: 'src/main/resources/static/js/dist',  // место для итогового js
        emptyOutDir: true,
        rollupOptions: {
            input: 'src/main/frontend/main.jsx',  // на основе него сборка
            output: {
                entryFileNames: 'bundle.js',  // итоговый файл
            }
        }
    }
})