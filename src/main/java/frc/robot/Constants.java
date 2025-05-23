// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkFlexConfig;

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
                public static final int kFrontLeftDrivingCanId = 1;
                public static final int kFrontRightDrivingCanId = 2;
                public static final int kRearLeftDrivingCanId = 4;    
                public static final int kRearRightDrivingCanId = 3;

                public static final int kFrontLeftTurningCanId = 5;    
                public static final int kFrontRightTurningCanId = 6;
                public static final int kRearLeftTurningCanId = 8;
                public static final int kRearRightTurningCanId = 7;


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
                public static final int kElevatorLeftCanId = 9;
                public static final int kElevatorRightCanId = 10;

                // DIO Port for Limit switch
                public static final int kElevatorLimitSwitchPort = 0;

                // Speed
                public static final double kElevatorSpeed = 1.0;

                // Height of each level, defined in motor rotations  
                // Coral Levels are 0-Stow, 1-CoralIntake, 2-L1, 3-L2, 4-L3, 5-L4
                public static final double[] corallevels = {5, 24.2, 24.2, 46.7, 133, 245};
                // Algae Levels are 0-Stow, 1-GroundIntake, 2-AlgaeProcessor, 3-L2, 4-L3, 5-Max
                public static final double[] algaelevels = {5, 10, 15, 55, 160, 245};

                public static final double kLowestLevel = 0.0;
                public static final double kHighestLevel = 245.0;

                public static final SparkFlexConfig leadConfig = new SparkFlexConfig();
                public static final SparkFlexConfig followConfig = new SparkFlexConfig();

                static {
                        
                        

                        leadConfig.smartCurrentLimit(50);
                        leadConfig.idleMode(IdleMode.kBrake);  
                        leadConfig.openLoopRampRate(2.0);   
                        leadConfig.closedLoopRampRate(0.0);   
                        

                        leadConfig.closedLoop.feedbackSensor(FeedbackSensor.kPrimaryEncoder);
                        leadConfig.closedLoop.pid(0.4, 0, 0.2);
                        leadConfig.closedLoop.outputRange(-1,1);
                        

                        followConfig.apply(leadConfig);
                        followConfig.inverted(true);
                }

        }    

        public static final class CoralConstants {
                // SPARK MAX CAN IDs
                public static final int kCoralCanID = 15;
                public static final int kWristCanID = 14;

                // Speed
                public static final double kCoralSpeed = 0.5;
                public static final double kCoralWristSpeed = 0.20;

                // Coral Wrist Levels are 0-Stow, 1-Intake, 2-L1-3Score, 3-L4Score
                public static final double[] coralwristlevels = {0, 11.66, 38.9, 29.3};

                public static final SparkMaxConfig coral = new SparkMaxConfig();
                public static final SparkMaxConfig wrist = new SparkMaxConfig();

                static {
                        
                        coral.smartCurrentLimit(20);
                        coral.idleMode(IdleMode.kBrake);        

                        coral.closedLoop.feedbackSensor(FeedbackSensor.kPrimaryEncoder);
                        coral.closedLoop.pid(0.1, 0, 0);
                        coral.closedLoop.outputRange(-1,1 );

                        wrist.smartCurrentLimit(50);
                        wrist.idleMode(IdleMode.kBrake);                    
                        wrist.closedLoopRampRate(1.0);            

                        wrist.closedLoop.feedbackSensor(FeedbackSensor.kPrimaryEncoder);
                        wrist.closedLoop.pid(0.1, 0, 0);
                        wrist.closedLoop.outputRange(-1, 1);

                }

        } 

        public static final class AlgaeConstants {
                // SPARK MAX CAN IDs
                public static final int kAlgaeWristCanID = 13;
                public static final int kAlgaeLeadCanID = 11;
                public static final int kAlgaeFollowCanID = 12;

                // Algae Wrist Levels are 0-Stowed, 1-Unfolded, 2-AimforBarge 
                public static final double[] algaewristlevels = {1.7, 40, 25}; // This needs to be calibrated manually and then modified

                // Speed
                public static final double kAlgaeSpeed = 0.5;
                public static final double kAlgaeWristSpeed = 0.5;

                public static final SparkMaxConfig AlgaeLead = new SparkMaxConfig();
                public static final SparkMaxConfig AlgaeFollow = new SparkMaxConfig();
                public static final SparkMaxConfig AlgaeWrist = new SparkMaxConfig();

                static {
                        
                        AlgaeLead.smartCurrentLimit(20);
                        AlgaeLead.idleMode(IdleMode.kBrake);        
                        
                        AlgaeLead.closedLoop.feedbackSensor(FeedbackSensor.kPrimaryEncoder);
                        AlgaeLead.closedLoop.pid(0.1, 0, 0);
                        AlgaeLead.closedLoop.outputRange(-1,1 );

                        AlgaeFollow.apply(AlgaeLead);
                        
                        AlgaeWrist.smartCurrentLimit(50);
                        AlgaeWrist.idleMode(IdleMode.kBrake);
                        AlgaeWrist.closedLoopRampRate(1.0);

                        AlgaeWrist.closedLoop.feedbackSensor(FeedbackSensor.kPrimaryEncoder);
                        AlgaeWrist.closedLoop.pid(0.1, 0, 0);
                        AlgaeWrist.closedLoop.outputRange(-1, 1);

                }

        } 
    
    

        public static final class OIConstants {
                public static final int kDriverControllerPort = 0;
                public static final double kDriveDeadband = 0.02;
                public static final double kDriverSpeedLimit = 0.90; // max forward speed
                public static final double kDriverRotationLimit = 0.80; // max rotational speed
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

        public static final class PhotonVisionConstants {
                public static final double kCameraHeight = 0.5; // Measured with a tape measure in meters
                public static final double kReefAprilTagDistance = 0.25;
                public static final double kReefAprilTagHeight = 0.2;
        }

}
