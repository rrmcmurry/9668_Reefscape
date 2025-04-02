package frc.robot.subsystems;

import frc.robot.Constants;
import frc.robot.Constants.ElevatorConstants;
import frc.utils.Common;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.*;
import java.util.Map;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;

public class ElevatorSubsystem extends SubsystemBase{

    private final SparkFlex m_ElevatorLeftSpark; 
    private final SparkFlex m_ElevatorRightSpark;
    private RelativeEncoder encoder;
    private NetworkTableEntry NTElevatorPosition;
    private NetworkTableEntry NTElevatorLevel;
    
    
    private double currentspeed;
    private double currentposition;
    public double elevatorspeedlimiter;
    private DigitalInput ElevatorLimitSwitch;
    private boolean isLimitPressed;
    private boolean wasLimitPressedLastTime;    
    private double speed;
    private double previousp;
    public int level;
    public boolean manualcontrol;
    private double kP;
    private double kD;




    public ElevatorSubsystem(){

        // Left Elevator Motor 
        m_ElevatorLeftSpark = new SparkFlex(ElevatorConstants.kElevatorLeftCanId, MotorType.kBrushless);
        m_ElevatorLeftSpark.configure(ElevatorConstants.leadConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        // Right Elevator Motor  
        ElevatorConstants.followConfig.follow(m_ElevatorLeftSpark, true);       
        m_ElevatorRightSpark = new SparkFlex(ElevatorConstants.kElevatorRightCanId, MotorType.kBrushless);   
        m_ElevatorRightSpark.configure(ElevatorConstants.followConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    
        // Elevator Encoder
        encoder = m_ElevatorLeftSpark.getEncoder();

        // Elevator Limit Switch returns true when open, false when closed
        ElevatorLimitSwitch = new DigitalInput(0);

        // Initialize NetworkTable variables
        NetworkTable ElevatorTable = NetworkTableInstance.getDefault().getTable("Elevator");
        NTElevatorPosition = ElevatorTable.getEntry("Position");
        NTElevatorLevel = ElevatorTable.getEntry("Level");
        
        wasLimitPressedLastTime = false;
        level = 0;
        previousp = 0;
    }

    public void init() {
        // Configure right Elevator Motor to follow left just in case this was missed at startup        
        ElevatorConstants.followConfig.follow(m_ElevatorLeftSpark, true);
        m_ElevatorRightSpark.configure(ElevatorConstants.followConfig, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);

        SmartDashboard.putNumber("Elevator kP", 0.1);  // Start values
        SmartDashboard.putNumber("Elevator kD", 0.0);

        manualcontrol = true;}


    private double scaledSpeedToTop() {
        return ElevatorConstants.kElevatorSpeed * Math.min(100,(ElevatorConstants.kHighestLevel - currentposition))/100; }
    
    private double scaledSpeedToBottom() {
        return -ElevatorConstants.kElevatorSpeed * Math.min(100, currentposition)/100; }

    public void robotPeriodic() {
        currentposition = encoder.getPosition(); 
        NTElevatorPosition.setDouble(currentposition);
        NTElevatorLevel.setInteger(level);
       
        // Reset encoder position to zero when limit switch is triggered (but don't do it over and over again)
        isLimitPressed = !ElevatorLimitSwitch.get();
        if (isLimitPressed && !wasLimitPressedLastTime) {
            encoder.setPosition(0.00);}
        wasLimitPressedLastTime = isLimitPressed;
        // Speed limiter used to limit swerve drive speed based on elevator height to prevent tipping with a higher center of gravity
        elevatorspeedlimiter = (Constants.ElevatorConstants.kHighestLevel + 50 - currentposition) / ( Constants.ElevatorConstants.kHighestLevel + 50); }

    public void teleopPeriodic(boolean coralmode) {
        if (!manualcontrol) {
            if (coralmode) goToCoralLevel(level); 
            else goToAlgaeLevel(level);}
        else {
            previousp = 0;}}

    public void raise() {
        manualcontrol = true;
        if (currentposition < ElevatorConstants.kHighestLevel) {
            currentspeed = scaledSpeedToTop();
            m_ElevatorLeftSpark.set(currentspeed);}
        else {
            m_ElevatorLeftSpark.stopMotor();}}

    public void lower() {        
        manualcontrol = true;
        if (currentposition > ElevatorConstants.kLowestLevel) {
            currentspeed = scaledSpeedToBottom();
            m_ElevatorLeftSpark.set(currentspeed);}
        else {
            m_ElevatorLeftSpark.stopMotor();}}

    public void stop() {
        m_ElevatorLeftSpark.stopMotor(); }

    public void goToCoralLevel(int level) {
        level = Common.clamp(level, 0, 5);
        goToPosition(ElevatorConstants.corallevels[level]);}

    public void goToAlgaeLevel(int level) {
        level = Common.clamp(level, 0, 5);
        goToPosition(ElevatorConstants.algaelevels[level]);}

    private void goToPosition(double targetposition) {
        kP = 0.6;
        kD = 0.05;

        currentposition = encoder.getPosition();
        double error = (targetposition - currentposition) / Math.max(Math.abs(targetposition), 1.0);  
        error = Common.clamp(error, -1.0, 1.0, 0.01);        
        double p = error * kP;  // PID - This is proportional 
        double d = (p - previousp) * kD;
        speed = p + d; 
        if (Math.abs(targetposition - currentposition) < 0.5) {  // If close enough to target, stop, otherwise set speed
            previousp = 0; 
            m_ElevatorLeftSpark.stopMotor();}
        else {
            previousp = p;
            m_ElevatorLeftSpark.set(speed);}}

    private void goToClosedLoopPosition(double targetposition){

    }
}
