package org.example.gps.visualization.animation; // Asegúrate que el paquete sea el correcto

import javafx.application.Platform;
import org.example.gps.visualization.GraphDisplay;

public abstract class BasedGraphAnimation implements Runnable {
    protected GraphDisplay graphDisplay;
    private String name;
    protected int delayDuration = 500;
    public OnAnimationFinished onFinishedCallback;

    public BasedGraphAnimation(GraphDisplay graphDisplay, String name) {
        this.graphDisplay = graphDisplay;
        this.name = name;
    }


    public abstract void playAnimation() throws InterruptedException;

    @Override
    public final void run() {
        try {

            playAnimation();
        } catch (InterruptedException e) {
            System.out.println("Animación '" + name + "' interrumpida.");
            Thread.currentThread().interrupt(); // Restaurar el estado de interrupción
        } finally {
            if (onFinishedCallback != null) {
                Platform.runLater(onFinishedCallback::execute);
            }
        }
    }

    public void setDelay(int milliseconds) {
        if (milliseconds > 0) {
            this.delayDuration = milliseconds;
        }
    }

    // Este método ya declara correctamente throws InterruptedException
    protected void delay() throws InterruptedException {
        Thread.sleep(delayDuration);
    }

    // Método helper para asegurar que las actualizaciones de UI se hacen en el hilo de JavaFX
    protected void requestRedraw() {
        if (graphDisplay != null) {
            Platform.runLater(() -> graphDisplay.draw());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}