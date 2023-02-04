// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.swervedrivespecialties.swervelib.Mk4ModuleConfiguration;
import com.swervedrivespecialties.swervelib.Mk4SwerveModuleHelper;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;
import com.swervedrivespecialties.swervelib.SwerveModule;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.util.Units;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */

public final class Constants {
   public enum CanBus {
      kRoboRIO("rio"),
      kCanivore("canivore");

      public final String bus_name;
      private CanBus(String value) {
         this.bus_name = value;
      }
   }

   public static class Drive {
      //: Physical constants (motors, sensors, ...)
      public static final double kDriveVoltageCompensation = 10;
      public static final double kWheelDiameterMeters = Units.inchesToMeters(4);
      public static final double kWheelBaseLengthMeters = Units.inchesToMeters(25.5);
      public static final double kTrackWidthMeters = Units.inchesToMeters(20.5);

      public static final double kDriveGearReduction = SdsModuleConfigurations.MK4_L2.getDriveReduction();

      public static final double kMaxDriveVelocityMetersPerSecond = 
         DCMotor.getNEO(1).freeSpeedRadPerSec / (2*Math.PI) * kDriveGearReduction * kWheelDiameterMeters * (kDriveVoltageCompensation/12.0);

      public static final double kMaxAngularVelocityRadiansPerSecond = 
         kMaxDriveVelocityMetersPerSecond / Math.hypot(kTrackWidthMeters / 2.0, kWheelBaseLengthMeters / 2.0);

      public static final SwerveDriveKinematics kDriveKinematics =
         new SwerveDriveKinematics(
            new Translation2d(kWheelBaseLengthMeters / 2, kTrackWidthMeters / 2),
            new Translation2d(kWheelBaseLengthMeters / 2, -kTrackWidthMeters / 2),
            new Translation2d(-kWheelBaseLengthMeters / 2, kTrackWidthMeters / 2),
            new Translation2d(-kWheelBaseLengthMeters / 2, -kTrackWidthMeters / 2));
      //: IMU constants
      public static final int kPigeonID = 0;
      public static final CanBus kPigeonCANBus = CanBus.kRoboRIO;

      public static class ModuleConfig {
         public int line, col;
         public int encoderID, steeringID, drivingID;
         public String name;
         public Rotation2d offset;

         public ModuleConfig(String name) {
            this.name = name;
         }

         public ModuleConfig setTab(int line, int col) {
            this.line = line; this.col = col;
            return this;
         }

         public ModuleConfig setSteeringID(int id) {
            this.steeringID = id; return this;
         }

         public ModuleConfig setEncoderID(int id) {
            this.encoderID = id; return this;
         }
         
         public ModuleConfig setDrivingID(int id) {
            this.drivingID = id; return this;
         }

         public ModuleConfig setOffset(double angle) {
            this.offset = Rotation2d.fromDegrees(angle); 
            return this;
         }
      }
      //: specific module config
      public static final ModuleConfig kFrontLeft = new ModuleConfig("Front Left")
         .setDrivingID(11)
         .setEncoderID(10)
         .setSteeringID(10)
         .setOffset(-112.5167456318007+180)
         .setTab(0, 0);

      public static final ModuleConfig kFrontRight = new ModuleConfig("Front Right")
         .setDrivingID(12)
         .setEncoderID(12)
         .setSteeringID(13)
         .setOffset(-121.9921875)
         .setTab(0, 2);

      public static final ModuleConfig kBackLeft = new ModuleConfig("Back Left")
         .setDrivingID(14)
         .setEncoderID(16)
         .setSteeringID(15)
         .setOffset(-53.349609375000014)
         .setTab(0, 4);

      public static final ModuleConfig kBackRight = new ModuleConfig("Back Right")
         .setDrivingID(17)
         .setEncoderID(14)
         .setSteeringID(16)
         .setOffset(-126.03515625)
         .setTab(0, 6);
   }


   public static class ArmJoint {
   public static final int kSolenoidChannelRatchet = 0;
   public static final int kMotorID = 10;
   public static final int kEncoderID = 10;
   }

   public static class ArmJoint2 {
      public static final int kMotorID = 21;
      public static final int kEncoderID = 21;
      public static final CanBus kMotorCANBus = CanBus.kCanivore; 
      public static final CanBus kEncoderCANBus = CanBus.kCanivore;

      public static final int kReverseSoftLimit = 695;
      public static final int kForwardSoftLimit = 1289;

      // These are fake gains; in actuality these must be determined individually for each robot
      public static final double kSVolts = 1;
      public static final double kGVolts = 1;
      public static final double kVVoltSecondPerRad = 0.5;
      public static final double kAVoltSecondSquaredPerRad = 0.1;

      public static final double kMaxVelocityRadPerSecond = 3;
      public static final double kMaxAccelerationRadPerSecSquared = 10;
   }
}