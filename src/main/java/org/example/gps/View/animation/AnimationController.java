package org.example.gps.View.animation;

import javafx.scene.control.Button; // Si vas a añadir botones de control

public class AnimationController {
    private Thread animationThread;
    private BasedGraphAnimation currentAnimation;

    // Para botones de Start/Stop, si los añades a la UI
    private Button startButton;
    private Button stopButton;

    public AnimationController() {
        // Constructor vacío, o puedes pasarle botones de UI aquí
    }

    // Constructor si tienes botones de UI para controlar
    // public AnimationController(Button startButton, Button stopButton) {
    //    this.startButton = startButton;
    //    this.stopButton = stopButton;
    //    if (this.stopButton != null) this.stopButton.setDisable(true);
    //    if (this.startButton != null) this.startButton.setDisable(true); // Habilitar cuando se elige una animación
    // }

    public boolean isAnimationRunning() {
        return animationThread != null && animationThread.isAlive();
    }

    public void startAnimation(BasedGraphAnimation animation, OnAnimationFinished onFinish) {
        if (isAnimationRunning()) {
            System.out.println("Ya hay una animación en curso.");
            return;
        }

        this.currentAnimation = animation;
        this.currentAnimation.onFinishedCallback = () -> {
            // Código que se ejecuta en el hilo de UI cuando la animación termina
            System.out.println("Animación '" + currentAnimation.getName() + "' finalizada.");
            if (stopButton != null) stopButton.setDisable(true);
            if (startButton != null) startButton.setDisable(false);
            if (onFinish != null) {
                onFinish.execute();
            }
            animationThread = null; // Limpiar la referencia al hilo
        };

        animationThread = new Thread(this.currentAnimation);
        animationThread.setDaemon(true); // Para que el hilo no impida que la app se cierre
        animationThread.start();

        if (startButton != null) startButton.setDisable(true);
        if (stopButton != null) stopButton.setDisable(false);
        System.out.println("Iniciando animación: " + currentAnimation.getName());
    }

    public void stopCurrentAnimation() {
        if (isAnimationRunning()) {
            System.out.println("Intentando detener la animación: " + currentAnimation.getName());
            animationThread.interrupt(); // Solicitar interrupción
            // El callback onFinished se encargará de reestablecer los botones.
            // No llames a onFinish.execute() aquí directamente porque la animación puede
            // tardar un momento en detenerse y limpiar a través de su bloque finally.
        } else {
            System.out.println("No hay animación en curso para detener.");
        }
    }
}