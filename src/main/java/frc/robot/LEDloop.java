package frc.robot;

// For the LED Thread
import java.lang.Runnable;
// Timer
import java.time.Duration;
import java.time.Instant; 
// Controller
import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.Timer;

public class LEDloop implements Runnable {
    private final Joystick m_stick = new Joystick(0);

    private static final int[] currentRGB = {100, 0, 0};

    // Timer
    private Instant lastChange = Instant.now(); 
    //delay ammount
    private final int delay = 7;

    // Only works with even divisors of 100 
    private final int colorSpeed = 1;

    public boolean running = true; 

    public LEDloop(){

    }

    @Override
    public void run(){
        while(!(Thread.interrupted())){
            if (Duration.between(lastChange, Instant.now()).toMillis() >= delay && !(m_stick.getRawButton(Robot.safe_Button))) {
                if (currentRGB[0] == 100 && currentRGB[1] < 100 && currentRGB[2] == 0) {
                    currentRGB[1] += colorSpeed;
                } else if (currentRGB[0] > 0 && currentRGB[1] == 100 && currentRGB[2] == 0) {
                    currentRGB[0] -= colorSpeed;
                } else if (currentRGB[0] == 0 && currentRGB[1] == 100 && currentRGB[2] < 100) {
                    currentRGB[2] += colorSpeed;
                } else if (currentRGB[0] == 0 && currentRGB[1] > 0 && currentRGB[2] == 100) {
                    currentRGB[1] -= colorSpeed;
                } else if (currentRGB[0] < 100 && currentRGB[1] == 0 && currentRGB[2] == 100) {
                    currentRGB[0] += colorSpeed;
                } else if (currentRGB[0] == 100 && currentRGB[1] == 0 && currentRGB[2] > 0) {
                    currentRGB[2] -= colorSpeed;
                } 
                Robot.setLEDColor(currentRGB[0], currentRGB[1], currentRGB[2]);
                lastChange = Instant.now();
            }            
        }
    }
    
}
    