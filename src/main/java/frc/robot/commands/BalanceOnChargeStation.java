// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.Drivetrain;

public class BalanceOnChargeStation extends CommandBase {

  private Drivetrain m_drivetrain;
  private int m_direction;
  // private enum State {
  //   kDriveOnFloor,
  //   kDriveOnRamp,
  //   kTryToLevel,
  //   kLevel
  // }

  /** Creates a new BalanceOnChargeStation. */
  public BalanceOnChargeStation(Drivetrain drivetrain, int direction) {
    m_drivetrain = drivetrain;
    m_direction = direction;
    // addRequirements(m_drivetrain);
  }

  // Called when the command is initially scheduled.
  @Override public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override public void execute() {
    final double kP = -0.01 *m_direction;
    double output = m_drivetrain.getRoll() * kP;

    if (Math.abs(output) > 1) {
      output = Math.copySign(1, output);
    } else if (Math.abs(output) < 0.5) {
      output = Math.copySign(0.5, output);
    }

    output = Math.copySign(1, m_direction);

    m_drivetrain.drive(output, 0, 0, false);
  }

  // Called once the command ends or is interrupted.
  @Override public void end(boolean interrupted) {
    m_drivetrain.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return Math.abs(m_drivetrain.getRoll()) < 6;
  }
}
