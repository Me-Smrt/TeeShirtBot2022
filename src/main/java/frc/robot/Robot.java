// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
//import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;

//import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;

// Import libraries below for PneumaticModule and Solenoid functionality
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
 
// Import libraries for NetworkTables
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

// Import libraries for LEDs
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;

//Thread
import java.lang.Thread;

/**
* The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot implements Runnable{
  private final static PWMSparkMax m_backLeftDrive = new PWMSparkMax(9);
  private final PWMSparkMax m_backRightDrive = new PWMSparkMax(7);
  private final PWMSparkMax m_frontLeftDrive = new PWMSparkMax(8);
  private final PWMSparkMax m_frontRightDrive = new PWMSparkMax(6);

  private final MecanumDrive m_robotDrive = new MecanumDrive(m_frontLeftDrive, m_backLeftDrive, m_frontRightDrive, m_backRightDrive);
  //private final PWMSparkMax cannon = new PWMSparkMax(5);
  //private final DifferentialDrive m_robotDrive = new DifferentialDrive(m_frontLeftDrive, m_frontRightDrive);
  public static final Joystick m_stick = new Joystick(0);
  public static final Timer m_timer = new Timer();

  public void run(){

  }
  LEDloop ledrainbow = new LEDloop();
  // Starts LED Thread 
  Thread Rainbow = new Thread(ledrainbow); 

  /*-------------------------------------*/
	// Network Tables 2022                 //
  /*-------------------------------------*/
  NetworkTableEntry ProgVer;
  
  /*-------------------------------------*/
	// LED 2022                            //
  /*-------------------------------------*/  
  public static AddressableLED m_led;
  public static AddressableLEDBuffer m_ledBuffer;
  public static int m_LED_R, m_LED_G, m_LED_B = 0;

  /*-------------------------------------*/
	// PNEUMATICS                          //
  // Solenoid Port Configuration 2022    //
  /*-------------------------------------*/
  private final Solenoid l_Solenoid = new Solenoid(PneumaticsModuleType.CTREPCM, 2);//2
  private final Solenoid m_Solenoid = new Solenoid(PneumaticsModuleType.CTREPCM, 1);//0
  private final Solenoid r_Solenoid = new Solenoid(PneumaticsModuleType.CTREPCM, 0);//1

  // Joystick Button Configuration 2022
  private static final int l_Button = 3;
  private static final int m_Button = 4;
  private static final int r_Button = 2;
  private static final int s_Button = 6;
  private static final int flightthumb_Button = 1;
  private static final int trigger_Button = 7;
  public static final int safe_Button = 5; // left bumper
  private boolean l_ButtonPressed, m_ButtonPressed, r_ButtonPressed, s_ButtonPressed,
                  flightthumb_ButtonPressed, trigger_ButtonPressed, safe_ButtonPressed = false;

  //Select fire mode, press and hold safety button, and pull trigger to fire

  //private final JoystickButton fireL = new JoystickButton(m_stick, 5);
  //private final JoystickButton fireM = new JoystickButton(m_stick, 3);
  //private final JoystickButton fireR = new JoystickButton(m_stick, 4);
  //private final JoystickButton fireS = new JoystickButton(m_stick, 6);
  
 	/*****************************************
	 * Code used to fire the cannons
	 *****************************************/
	// Fires left-barrel
	public void LeftShot(){
    l_Solenoid.setPulseDuration(0.05);
    l_Solenoid.startPulse();
    l_Solenoid.set(false);
    l_Solenoid.set(true);
    closeValves();
  } 

  // Fires middle-barrel
  public void MiddleShot() {
    m_Solenoid.setPulseDuration(0.05);
    m_Solenoid.startPulse();
    m_Solenoid.set(false);
    m_Solenoid.set(true);
    closeValves();
  }

  // Fires right-barrel
  public void RightShot(){
    r_Solenoid.setPulseDuration(0.05);
    r_Solenoid.startPulse();
    r_Solenoid.set(false);
    r_Solenoid.set(true);
    closeValves();
  }

  // Fires salvo
  public void SalvoShot() {
    LeftShot();
    MiddleShot();
    RightShot();
  }

  public void closeValves(){
    l_Solenoid.set(false);
    m_Solenoid.set(false);
    r_Solenoid.set(false);
  }
  /* ------------------------------------*/

  /*****************************************
	 * Code used to fire the cannons
	 *****************************************/
  public static void setLEDColor(int m_LED_R, int m_LED_G, int m_LED_B){
      /*** LED Lighting ***/
      for (var j = 0; j < m_ledBuffer.getLength(); j++) {
        // Sets the specified LED to the RGB values for color
        m_ledBuffer.setRGB(j, m_LED_R, m_LED_G, m_LED_B);
      }
      m_led.setData(m_ledBuffer);
  }

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_backRightDrive.setInverted(true);
    m_frontRightDrive.setInverted(true);

    // Set up LEDs on port 1
    m_led = new AddressableLED(1);
    // Default buffer to a length of 60, start empty output
    // Length is expensive to set, so only set it once, then just update data
    m_ledBuffer = new AddressableLEDBuffer(88); //currently 88 LEDs wired together
    m_led.setLength(m_ledBuffer.getLength());
    // Set the data
    
    Rainbow.start();
    
    m_led.setData(m_ledBuffer);
    m_led.start();



    // NetworkTable Set Program Version
    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    NetworkTable table = inst.getTable("datatable");
    table.getEntry("ProgVer").setString("0.0.3");
  }

  /** This function is run once each time the robot enters autonomous mode. */
  @Override
  public void autonomousInit() {
    m_timer.reset();
    m_timer.start();
  }

  /* This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    // Drive for 2 seconds
    if (m_timer.get() < 2.0) {
      //m_robotDrive.arcadeDrive(0.5, 0.0); // drive forwards half speed
    } else {
      m_robotDrive.stopMotor(); // stop robot
    }

    l_Solenoid.set(false);
    m_Solenoid.set(false);
    r_Solenoid.set(false);

    l_Solenoid.set(true);
    m_Solenoid.set(true);
    r_Solenoid.set(true);

  }

  /** This function is called once each time the robot enters teleoperated mode. */
  @Override
  public void teleopInit() {
    safe_ButtonPressed = false;
    //l_Solenoid.set(true);
    //m_Solenoid.set(true);
    //r_Solenoid.set(true);
    closeValves();
  }
  public void hasInput(Joystick controller) {
    
  }
  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
    //safe_ButtonPressed = false;
    //m_robotDrive.arcadeDrive(m_stick.getY(), m_stick.getX());

    //m_frontLeftDrive.set(m_stick.getRawAxis(0));
    //m_frontRightDrive.set(m_stick.getRawAxis(1));
    //m_backLeftDrive.set(m_stick.getRawAxis(4));
    //m_backRightDrive.set(m_stick.getRawAxis(5));

    double speed = 0.5;

    m_robotDrive.driveCartesian(-speed*m_stick.getRawAxis(1), speed*m_stick.getRawAxis(0), speed*m_stick.getRawAxis(4));//, gyroAngle);


    /*** Firing Control ***/
    // Syntax below requires methods to be established as commands
    //fireL.whenPressed(new LeftShot());
		//fireM.whenPressed(new MiddleShot());
		//fireR.whenPressed(new RightShot());
    //fireS.whenPressed(new SalvoShot());
    
    // Alternate method applied 
    // Paired if statement check for and ignore long button presses
    // so a single long press does not count as multiple presses
    /*if (m_stick.getRawButton(safe_Button)){
      ledrainbow.running = false;
    }else if(!(Rainbow.isAlive())){
      ledrainbow.running = true; 
    }*/
    
    if (m_stick.getRawButton(safe_Button) && safe_ButtonPressed == true) {
      safe_ButtonPressed = false;
      setLEDColor(255,0,0);
      System.out.println("Safe Button is False");
    }
    if (!m_stick.getRawButton(safe_Button) && safe_ButtonPressed == false) {
      safe_ButtonPressed = true;
      setLEDColor(0,0,0);
      System.out.println("Safe Button is True");
    }
    if (m_stick.getRawButton(l_Button) && !l_ButtonPressed && !safe_ButtonPressed) {
      //m_stick.setRumble(RumbleType.kLeftRumble, 1);
      //m_stick.setRumble(RumbleType.kRightRumble, 1);
      for(int i = 0; i < 255; i++) {
        Timer.delay(0.001);
        setLEDColor(0,0,i);
      }
      //Timer.delay(0.6);
      if(!safe_ButtonPressed) {
        setLEDColor(255,0,0);
      } else {
        setLEDColor(0,0,0);
      }
      LeftShot();
      l_ButtonPressed = true;
    }
    if (!m_stick.getRawButton(l_Button) && l_ButtonPressed ) {
      l_ButtonPressed = false;
    }

    if (m_stick.getRawButton(m_Button) && !m_ButtonPressed  && !safe_ButtonPressed) {
      for(int i = 0; i < 255; i++) {
        Timer.delay(0.001);
        setLEDColor(i,i,0);
      }
      //Timer.delay(0.6);
      if(!safe_ButtonPressed) {
        setLEDColor(255,0,0);
      } else {
        setLEDColor(0,0,0);
      }
      MiddleShot();
      m_ButtonPressed = true;
    }
    if (!m_stick.getRawButton(m_Button) && m_ButtonPressed) {
      m_ButtonPressed = false;
    }

    if (m_stick.getRawButton(r_Button) && !r_ButtonPressed && !safe_ButtonPressed) {
      
      for(int i = 0; i < 255; i++) {
        Timer.delay(0.001);
        setLEDColor(i,0,i);
      }
      //Timer.delay(0.6);
      if(!safe_ButtonPressed) {
        setLEDColor(255,0,0);
      } else {
        setLEDColor(0,0,0);
      }
      RightShot();
      r_ButtonPressed = true;
    }
    if (!m_stick.getRawButton(r_Button) && r_ButtonPressed) {
      r_ButtonPressed = false;
    }

    if (m_stick.getRawButton(s_Button) && !s_ButtonPressed ){//&& !safe_ButtonPressed) { //comment back in if salvo becomes functional
      if(!safe_ButtonPressed) {
        setLEDColor(255,0,0);
      } else {
        setLEDColor(0,0,0);
      }
      //SalvoShot();
      s_ButtonPressed = true;
      
    }
    if (!m_stick.getRawButton(s_Button) && s_ButtonPressed) {
      s_ButtonPressed = false;
    }

    if (m_stick.getRawButton(flightthumb_Button) && !flightthumb_ButtonPressed) {
      setLEDColor(0,0,0);
      flightthumb_ButtonPressed = true;
    }
    if (!m_stick.getRawButton(flightthumb_Button) && flightthumb_ButtonPressed) {
      flightthumb_ButtonPressed = false;
    }

    if (m_stick.getRawButton(trigger_Button) && !trigger_ButtonPressed) {
      setLEDColor(255,0,0);
      trigger_ButtonPressed = true;
    }
    if (!m_stick.getRawButton(trigger_Button) && trigger_ButtonPressed) {
      trigger_ButtonPressed = false;
    }
  }
  
  /** This function is called once each time the robot enters test mode. */
  @Override
  public void testInit() {
    /*Thread Rainbow = new Thread(new LEDloop()); 
    Rainbow.start();
    Rainbow.interrupt();*/
    l_Solenoid.setPulseDuration(0.05);
    l_Solenoid.startPulse();
    setLEDColor(0,0,0);
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
    
  /* double speed = 0.25;
  
    m_frontLeftDrive.set(speed);
    m_frontRightDrive.set(speed);
    m_backLeftDrive.set(speed);
    m_backRightDrive.set(speed);
  */

  r_Solenoid.set(false);
  r_Solenoid.set(true);
  }
}
