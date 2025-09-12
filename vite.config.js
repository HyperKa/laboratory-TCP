import { defineConfig } from "vite";
import react from '@vitejs/plugin-react';

export default defineConfig({
    plugins: [react()],
    build: {
        outDir: 'src/main/resources/static/js/dist', // Куда класть итоговый JS
        emptyOutDir: true,
        rollupOptions: {
            input: 'src/main/frontend/main.jsx', // Что собирать
            output: {
                entryFileNames: 'bundle.js', // Как назвать итоговый файл
            }
        }
    }
});