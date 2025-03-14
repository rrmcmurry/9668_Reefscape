// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants
 */
public final class Constants {
  public static final class DriveConstants {
    // Driving Parameters 
    public static final double kMaxSpeedMetersPerSecond = 4.8; // Default is 4.8 meters per second     
    public static final double kMaxAngularSpeed = 2 * Math.PI; // Default is 2 PI radians (one full rotation) per second 

    public static final double kDirectionSlewRate = 1.2; // radians per second
    public static final double kMagnitudeSlewRate = 1.8; // percent per second (1 = 100%)
    public static final double kRotationalSlewRate = 2.0; // percent per second (1 = 100%)
    
    // Chassis configuration
    public static final double kTrackWidth = Units.inchesToMeters(26.5);
    public static final double kWheelBase = Units.inchesToMeters(26.5);

    // Relative positions from center
    public static final SwerveDriveKinematics kDriveKinematics = new SwerveDriveKinematics(
        new Translation2d(kWheelBase / 2, kTrackWidth / 2),
        new Translation2d(kWheelBase / 2, -kTrackWidth / 2),
        new Translation2d(-kWheelBase / 2, kTrackWidth / 2),
        new Translation2d(-kWheelBase / 2, -kTrackWidth / 2));

    // Angular offsets of the modules relative to the chassis in radians
    public static final double kFrontLeftChassisAngularOffset = -Math.PI / 2;
    public static final double kFrontRightChassisAngularOffset = 0;
    public static final double kBackLeftChassisAngularOffset = Math.PI;
    public static final double kBackRightChassisAngularOffset = Math.PI / 2;

    // SPARK MAX CAN IDs
    public static final int kFrontLeftDrivingCanId = 3;
    public static final int kFrontRightDrivingCanId = 4;
    public static final int kRearLeftDrivingCanId = 1;    
    public static final int kRearRightDrivingCanId = 2;

    public static final int kFrontLeftTurningCanId = 8;    
    public static final int kFrontRightTurningCanId = 7;
    public static final int kRearLeftTurningCanId = 5;
    public static final int kRearRightTurningCanId = 6;
    
    
    public static final double kUnitstoFeet = 4.2;
  }

  public static final class ModuleConstants {
    // The MAXSwerve module can be configured with one of three pinion gears: 12T, 13T, or 14T.
    // This changes the drive speed of the module (a pinion gear with more teeth will result in a
    // robot that drives faster).
    public static final int kDrivingMotorPinionTeeth = 14;

    // Invert the turning encoder, since the output shaft rotates in the opposite direction of
    // the steering motor in the MAXSwerve Module.
    public static final boolean kTurningEncoderInverted = true;

    // Calculations required for driving motor conversion factors and feed forward
    public static final double kDrivingMotorFreeSpeedRps = NeoMotorConstants.kFreeSpeedRpm / 60;
    public static final double kWheelDiameterMeters = 0.0762;
    public static final double kWheelCircumferenceMeters = kWheelDiameterMeters * Math.PI;
    // 45 teeth on the wheel's bevel gear, 22 teeth on the first-stage spur gear, 15 teeth on the bevel pinion
    public static final double kDrivingMotorReduction = (45.0 * 22) / (kDrivingMotorPinionTeeth * 15);
    public static final double kDriveWheelFreeSpeedRps = (kDrivingMotorFreeSpeedRps * kWheelCircumferenceMeters) / kDrivingMotorReduction;
    public static final double kDrivingEncoderPositionFactor = (kWheelDiameterMeters * Math.PI) / kDrivingMotorReduction; // meters
    public static final double kDrivingEncoderVelocityFactor = ((kWheelDiameterMeters * Math.PI) / kDrivingMotorReduction) / 60.0; // meters per second

    public static final double kTurningEncoderPositionFactor = (2 * Math.PI); // radians
    public static final double kTurningEncoderVelocityFactor = (2 * Math.PI) / 60.0; // radians per second

    public static final double kTurningEncoderPositionPIDMinInput = 0; // radians
    public static final double kTurningEncoderPositionPIDMaxInput = kTurningEncoderPositionFactor; // radians

     // Create SparkMax configurations for the Motor controllers
    public static final SparkMaxConfig drivingConfig = new SparkMaxConfig();
    public static final SparkMaxConfig turningConfig = new SparkMaxConfig();

    static {
        // Use module constants to calculate conversion factors and feed forward gain.
        double drivingFactor = kWheelDiameterMeters * Math.PI / kDrivingMotorReduction;
        double turningFactor = 2 * Math.PI;
        double drivingVelocityFeedForward = 1 / kDriveWheelFreeSpeedRps;

        drivingConfig
                .idleMode(IdleMode.kCoast)
                .smartCurrentLimit(50);
        drivingConfig.encoder
                .positionConversionFactor(drivingFactor) // meters
                .velocityConversionFactor(drivingFactor / 60.0); // meters per second
        drivingConfig.closedLoop
                .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                // These are example gains you may need to tune them for your own robot!
                .pid(0.04, 0, 0)
                .velocityFF(drivingVelocityFeedForward)
                .outputRange(-1, 1);

        turningConfig
                .idleMode(IdleMode.kBrake)
                .smartCurrentLimit(20);
        turningConfig.absoluteEncoder
                .inverted(true)
                .positionConversionFactor(turningFactor) // radians
                .velocityConversionFactor(turningFactor / 60.0); // radians per second
        turningConfig.closedLoop
                .feedbackSensor(FeedbackSensor.kAbsoluteEncoder)
                // These are example gains you may need to tune them for your own robot!
                .pid(1, 0, 0)
                .outputRange(-1, 1)
                .positionWrappingEnabled(true)
                .positionWrappingInputRange(0, turningFactor);
    }
  }

  public static final class ElevatorConstants {
    // SPARK MAX CAN IDs
    public static final int kElevatorLeftCanId = 10;
    public static final int kElevatorRightCanId = 11;
    
    // DIO Port for Limit switch
    public static final int kElevatorLimitSwitchPort = 0;

    // Speed
    public static final double kElevatorSpeed = 0.5;

    // Height of each level, defined in motor rotations  
    // NEED TO TEST AND ADJUST THESE VALUES

    public static final double[] levels = {0, 19, 100, 180, 260};

    public static final double kLowestLevel = 0.0;
    public static final double kHighestLevel = 260.0;

    public static final double kCoralLevel1 = 3.0 * 260 / 42;
    public static final double kCoralLevel2 = 16.0 * 260 / 42;
    public static final double kCoralLevel3 = 29.0 * 260 / 42;
    public static final double kCoralLevel4 = 42.0 * 260 /42;

    public static final double kIntakeLevel = 20.0 * 260 / 42;

    public static final double kAlgaeLevel1 = 20.0 * 260 / 42;
    public static final double kAlgaeLevel2 = 35.0 * 260 / 42;

    public static final SparkMaxConfig leadConfig = new SparkMaxConfig();
    public static final SparkMaxConfig followConfig = new SparkMaxConfig();

    static {
            
            leadConfig.smartCurrentLimit(50);
            leadConfig.idleMode(IdleMode.kBrake);  
            leadConfig.openLoopRampRate(2.0);      
        
            leadConfig.closedLoop.feedbackSensor(FeedbackSensor.kPrimaryEncoder);
            leadConfig.closedLoop.pid(0.6, 0, 0);
            leadConfig.closedLoop.outputRange(-1,1);
            
        
            followConfig.apply(leadConfig);
            followConfig.inverted(true);
    }

  }    

  public static final class CoralConstants {
    // SPARK MAX CAN IDs
    public static final int kCoralCanID = 13;
    public static final int kWristCanID = 12;

    // Speed
    public static final double kCoralSpeed = 0.05;

    public static final SparkMaxConfig coral = new SparkMaxConfig();
    public static final SparkMaxConfig wrist = new SparkMaxConfig();

    static {
            
            coral
                    .smartCurrentLimit(20)
                    .idleMode(IdleMode.kBrake);        
        
            coral.closedLoop
                    .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                    .pid(0.1, 0, 0)
                    .outputRange(-1,1 );
        
            wrist  
                    .smartCurrentLimit(50)
                    .idleMode(IdleMode.kBrake);

            wrist.closedLoop 
                    .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                    .pid(0.1, 0, 0)
                    .outputRange(-1, 1);

    }

  } 

  public static final class AlgaeConstants {
        // SPARK MAX CAN IDs
        public static final int kAlgaeWristCanID = 14;
        public static final int kAlgaeLeadCanID = 15;
        public static final int kAlgaeFollowCanID = 16;
    
        // Speed
        public static final double kAlgaeSpeed = 0.5;
        public static final double kAlgaeWristSpeed = 0.15;
    
        public static final SparkMaxConfig AlgaeLead = new SparkMaxConfig();
        public static final SparkMaxConfig AlgaeFollow = new SparkMaxConfig();
        public static final SparkMaxConfig AlgaeWrist = new SparkMaxConfig();

        static {
                
                AlgaeLead
                        .smartCurrentLimit(20)
                        .idleMode(IdleMode.kBrake);        
            
                AlgaeLead.closedLoop
                        .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                        .pid(0.1, 0, 0)
                        .outputRange(-1,1 );

                AlgaeFollow.apply(AlgaeLead);
            
                AlgaeWrist  
                        .smartCurrentLimit(50)
                        .idleMode(IdleMode.kBrake);
    
                AlgaeWrist.closedLoop 
                        .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                        .pid(0.1, 0, 0)
                        .outputRange(-1, 1);
    
        }
    
      } 
    
    

  public static final class OIConstants {
    public static final int kDriverControllerPort = 0;
    public static final double kDriveDeadband = 0.02;
    public static final double kDriverSpeedLimit = 0.25; // 50% of max speed
    public static final double kDriverRotationLimit = 0.25; // 30% of max speed
  }

  public static final class AutoConstants {
    public static final double kMaxSpeedMetersPerSecond = 1;
    public static final double kMaxAccelerationMetersPerSecondSquared = 3;
    public static final double kMaxAngularSpeedRadiansPerSecond = Math.PI;
    public static final double kMaxAngularSpeedRadiansPerSecondSquared = Math.PI;

    public static final double kPXController = 1;
    public static final double kPYController = 1;
    public static final double kPThetaController = 1;

    // Constraint for the motion profiled robot angle controller
    public static final TrapezoidProfile.Constraints kThetaControllerConstraints = new TrapezoidProfile.Constraints(
        kMaxAngularSpeedRadiansPerSecond, kMaxAngularSpeedRadiansPerSecondSquared);
  }

  public static final class NeoMotorConstants {
    public static final double kFreeSpeedRpm = 5676;
  }
}
