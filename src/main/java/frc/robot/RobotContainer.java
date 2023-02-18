// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.List;

import com.ctre.phoenix.motion.BuffTrajPointStreamJNI;
import com.kennedyrobotics.auto.AutoSelector;
import com.kennedyrobotics.hardware.misc.RevDigit;
import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.auto.PIDConstants;
import com.pathplanner.lib.auto.SwerveAutoBuilder;
import com.pathplanner.lib.server.PathPlannerServer;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.ArmJoint1Outward;
import frc.robot.commands.ArmJoint2Inward;
import frc.robot.commands.ArmJoint2Outward;
import frc.robot.commands.DriveWithController;
import frc.robot.commands.MoveArmjoint1ToPosition;
import frc.robot.commands.MoveArmjoint2;
import frc.robot.commands.MoveArmsToPickUpPosition;
import frc.robot.commands.MoveArmsToSecondConePosition;
import frc.robot.commands.MoveArmsToStartingPosition;
import frc.robot.commands.MoveWristJoint2;
import frc.robot.commands.WristJointInward2;
import frc.robot.commands.WristJointOutward2;
import frc.robot.subsystems.ArmJoint1;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.GripperSubsystem;
import frc.robot.subsystems.ProfiledArmjoint;
import frc.robot.subsystems.WristJoint;
import frc.robot.subsystems.WristJointV2;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  //OI
  private final CommandXboxController m_controller = new CommandXboxController(0);
  private final CommandJoystick m_buttonPad = new CommandJoystick(1);
  // The robot's subsystems and commands are defined here...
  private final Drivetrain m_drivetrain = new Drivetrain();
  private final ArmJoint1 m_armJoint1 = new ArmJoint1();
  private final ProfiledArmjoint m_Armjoint2 = new ProfiledArmjoint(Constants.PidArmCfg.kArmjoint2, () -> m_armJoint1.getAngle().getDegrees());
  private final GripperSubsystem m_Gripper = new GripperSubsystem();
  private final PneumaticHub m_PneumaticHub = new PneumaticHub();
  private final WristJointV2 m_Wrist = new WristJointV2(Constants.PidArmCfg.kWrist, () -> m_Armjoint2.getAngleFromGround().getDegrees());

	//Auto
	private final RevDigit m_revDigit;
	private final AutoSelector m_autoSelector;

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    //Configure default commands
    m_armJoint1.setDefaultCommand(new RunCommand(
      () -> m_armJoint1.stop(),
      m_armJoint1
    ));

    m_Gripper.setDefaultCommand(new RunCommand(
      () -> m_Gripper.openGripper(),
      m_Gripper
    ));
    


    // Configure the button bindings
    configureButtonBindings();

    m_PneumaticHub.enableCompressorAnalog(90, 120);

		PathPlannerServer.startServer(5811);

		//Auto Selector
		m_revDigit = new RevDigit();
		m_revDigit.display("BWMP");
		m_autoSelector = new AutoSelector(m_revDigit, "DFLT", new SequentialCommandGroup(
			new PrintCommand("OOPS")
			));

		//Initialize other autos here
		m_autoSelector.registerCommand("Auto Crap - Community", "CRAP", new SequentialCommandGroup(
			new RunCommand(() -> m_drivetrain.drive(1, 0, 0, false), m_drivetrain).withTimeout(1.5), 
			new InstantCommand(() -> m_drivetrain.stop())
		));
		
		m_autoSelector.initialize();
	}

 	/**
		* Use this method to define your button->command mappings. Buttons can be created by
		* instantiating a {@link GenericHID} or one of its subclasses ({@link
		* edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
		* edu.wpi.first.wpilibj2.command.button.JoystickButton}.
		*/
  private void configureButtonBindings() {
    // Configure default commands
    m_drivetrain.setDefaultCommand(new DriveWithController(m_drivetrain, m_controller.getHID()));

    m_controller.start().onTrue(new InstantCommand(m_drivetrain::resetHeading)); // TODO this should also do something with odometry? As it freaks out
  
     m_controller.rightStick().toggleOnTrue(new RunCommand(()->{
        var latchedModuleStates = new SwerveModuleState[]{
          new SwerveModuleState(0, Rotation2d.fromDegrees(45)),
          new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
          new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
          new SwerveModuleState(0, Rotation2d.fromDegrees(45)),
      };


      m_drivetrain.setModuleStates(latchedModuleStates);
     }, m_drivetrain));

     m_controller.y().toggleOnTrue(new RunCommand(()->m_Gripper.closeGripper(),m_Gripper));


     m_buttonPad.button(1).whileTrue(new ArmJoint1Outward(m_armJoint1));
     m_buttonPad.button(5).whileTrue(new RunCommand(()->m_armJoint1.inwards(), m_armJoint1));
     m_buttonPad.button(9).onTrue(new MoveArmjoint1ToPosition(m_armJoint1, Rotation2d.fromDegrees(60)));

     m_buttonPad.button(2).whileTrue(new ArmJoint2Outward(m_Armjoint2));
     m_buttonPad.button(6).whileTrue(new ArmJoint2Inward(m_Armjoint2));
     m_buttonPad.button(10).onTrue(new MoveArmjoint2(m_Armjoint2, 0));

     m_buttonPad.button(3).whileTrue(new WristJointOutward2(m_Wrist));
     m_buttonPad.button(7).whileTrue(new WristJointInward2(m_Wrist));
     m_buttonPad.button(11).onTrue(new MoveWristJoint2(m_Wrist, 0));

	 m_buttonPad.button(8).onTrue(new MoveArmsToStartingPosition(m_armJoint1, m_Armjoint2, m_Wrist));
	 m_buttonPad.button(12).onTrue(new MoveArmsToPickUpPosition(m_armJoint1, m_Armjoint2, m_Wrist));
	 m_buttonPad.button(4).onTrue(new MoveArmsToSecondConePosition(m_armJoint1, m_Armjoint2, m_Wrist));
  }
  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return m_autoSelector.selected();
  }



	public Command makeWPILibSwerveExamople() {
		TrajectoryConfig config = 
			new TrajectoryConfig(1, 1)
			.setKinematics(Constants.Drive.kDriveKinematics);

		Trajectory exampleTrajectory = TrajectoryGenerator.generateTrajectory(
			//: origin faces the positive x direction
			new Pose2d(0, 0, new Rotation2d(0)),
			//: pass through 2 'waypoints' that create an 'S' shaped path
			List.of(new Translation2d(Units.inchesToMeters(24), Units.inchesToMeters(24))),
			//: end 3 meters ahead of our starting position
			new Pose2d(Units.inchesToMeters(48), 0, new Rotation2d(0)),
			//: pass through the trajectory configuration
			config
		);

		var thetaController = new ProfiledPIDController(
			Constants.Auto.kPThetaController, 0, 0, Constants.Auto.kThetaControllerConstraints
		);
		thetaController.enableContinuousInput(-Math.PI, Math.PI);

		SwerveControllerCommand swerveControllerCommand = new SwerveControllerCommand(
			exampleTrajectory,
			m_drivetrain::getPose,
			Constants.Drive.kDriveKinematics,

			//:  Position Controllers
			new PIDController(3, 0, 0),
			new PIDController(3, 0, 0),

			thetaController,
			m_drivetrain::setModuleStates,
			m_drivetrain
		);

		m_drivetrain.resetOdometry(exampleTrajectory.getInitialPose());

		return new SequentialCommandGroup(
			new InstantCommand(() -> m_drivetrain.resetOdometry(exampleTrajectory.getInitialPose())),
			swerveControllerCommand.andThen(() -> m_drivetrain.drive(0, 0, 0, false))
		);		
	}

	public Command createPathPlanner() {
		List<PathPlannerTrajectory> pathGroup = PathPlanner.loadPathGroup("FullAuto", new PathConstraints(4, 3));
		SwerveAutoBuilder autoBuilder = new SwerveAutoBuilder(
			m_drivetrain::getPose,
			m_drivetrain::resetOdometry,
			Constants.Drive.kDriveKinematics,

			new PIDConstants(5.0, 0, 0),
			new PIDConstants(0.5, 0, 0),
			
			m_drivetrain::setModuleStates,
			Constants.eventMap,
			true, //: The path automatically mirrors depending on alliance color
			m_drivetrain
		);
		
		return autoBuilder.fullAuto(pathGroup);
	}
  public void autonomousInit(){
    m_drivetrain.resetHeading();
  }
}

