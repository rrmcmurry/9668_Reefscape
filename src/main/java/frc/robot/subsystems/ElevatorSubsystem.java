package frc.robot.subsystems;

import frc.robot.Constants.ElevatorConstants;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import edu.wpi.first.wpilibj.DigitalInput;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkClosedLoopController;

public class ElevatorSubsystem extends SubsystemBase{

    private final SparkMax m_ElevatorLeftSpark; 
    private final SparkMax m_ElevatorRightSpark;
    private final DigitalInput m_ElevatorLimitSwitch; 
    
    private SparkClosedLoopController closedLoopController;
    private RelativeEncoder encoder;
      // NetworkTable Entries for Position
    private NetworkTableEntry NTElevatorPosition;

    public ElevatorSubsystem(){

        // Left Elevator Motor 
        m_ElevatorLeftSpark = new SparkMax(ElevatorConstants.kElevatorLeftCanId, MotorType.kBrushless);
        m_ElevatorLeftSpark.configure(ElevatorConstants.leadConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        // Right Elevator Motor  
        ElevatorConstants.followConfig.follow(m_ElevatorLeftSpark, true);       
        m_ElevatorRightSpark = new SparkMax(ElevatorConstants.kElevatorRightCanId, MotorType.kBrushless);   
        m_ElevatorRightSpark.configure(ElevatorConstants.followConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        
        // Elevator Limit Switch returns true when when open false when circuit is closed
        // Wire this as normally closed
        m_ElevatorLimitSwitch = new DigitalInput(ElevatorConstants.kElevatorLimitSwitchPort);

        // PID Controller
        closedLoopController = m_ElevatorLeftSpark.getClosedLoopController();

        // Elevator Encoder
        encoder = m_ElevatorLeftSpark.getEncoder();

        // Initialize NetworkTable variables
        NetworkTable ElevatorTable = NetworkTableInstance.getDefault().getTable("Elevator");
        NTElevatorPosition = ElevatorTable.getEntry("Position");

    }

    public void init() {
        // Right Elevator Motor  
        ElevatorConstants.followConfig.follow(m_ElevatorLeftSpark, true);
        m_ElevatorRightSpark.configure(ElevatorConstants.followConfig, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
    }

    public double getPosition() {
        // Right now I'm assuming we'll start by using the left SparkMax motor encoder to determine position, but we might want to add something 
        // like a linear magnetic encoder or string potentiometer for more accuracy. 
        return encoder.getPosition();
    }

    public void raise() {
        m_ElevatorLeftSpark.set(ElevatorConstants.kElevatorSpeed);
        NTElevatorPosition.setDouble(getPosition());
    }

    public void stop() {
        m_ElevatorLeftSpark.stopMotor();    
    }

    public void lower() {        
        if (!m_ElevatorLimitSwitch.get()) {
            m_ElevatorLeftSpark.set(-ElevatorConstants.kElevatorSpeed);
        }
        else {        
            m_ElevatorLeftSpark.stopMotor();        
        }
        NTElevatorPosition.setDouble(getPosition());
    }

    public void goToCoralLevel(int level) {

        if (level == 0) {
            closedLoopController.setReference(ElevatorConstants.kLowestLevel,  ControlType.kPosition);            
        }
        else if (level == 1) {
            closedLoopController.setReference(ElevatorConstants.kCoralLevel1,  ControlType.kPosition);            
        }
        else if (level == 2) {
            closedLoopController.setReference(ElevatorConstants.kCoralLevel2,  ControlType.kPosition);            
        }
        else if (level == 3) {
            closedLoopController.setReference(ElevatorConstants.kCoralLevel3,  ControlType.kPosition);            
        }
        else if (level == 4) {
            closedLoopController.setReference(ElevatorConstants.kCoralLevel4,  ControlType.kPosition);            
        }
    }
    public void goToAlgaeLevel(int level) {

        if (level == 0) {
            closedLoopController.setReference(ElevatorConstants.kLowestLevel,  ControlType.kPosition);            
        }
        else if (level == 1) {
            closedLoopController.setReference(ElevatorConstants.kAlgaeLevel1,  ControlType.kPosition);            
        }
        else if (level == 2) {
            closedLoopController.setReference(ElevatorConstants.kAlgaeLevel2,  ControlType.kPosition);            
        }
    }

    public void goToIntakeLevel() {
        closedLoopController.setReference(ElevatorConstants.kIntakeLevel,  ControlType.kPosition);        
    }

    public void goToLevel(int level) {             
        closedLoopController.setReference(ElevatorConstants.levels[level], ControlType.kPosition);
    }

}
