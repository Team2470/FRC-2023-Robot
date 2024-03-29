// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.ArmJoint1;

public class ArmJoint1Outward extends CommandBase {
  private enum OutwardState{
    kWaitForRatchet,
    KRunning
  }
  
  private final ArmJoint1 m_armJoint1;
  private final Timer m_timer = new Timer();
  private OutwardState m_armstate;
  
  /** Creates a new ArmJoint1Outward. */
  public ArmJoint1Outward(ArmJoint1 armjoint) {
    m_armJoint1 = armjoint;

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_armJoint1);
  }

  // Called when the command is initially scheduled.
  @Override public void initialize() {
    m_armstate = OutwardState.kWaitForRatchet;
    m_timer.reset();
    m_timer.start();

  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override public void execute() {
    m_armJoint1.engageRatchet(false);
    switch(m_armstate){
      case kWaitForRatchet:
        if (m_timer.advanceIfElapsed(0.1)){
          m_armstate = OutwardState.KRunning;
        }
        break;
      case KRunning:
        m_armJoint1.outwards();
        break;
    }
  }

  // Called once the command ends or is interrupted.
  @Override public void end(boolean interrupted) {
    m_armJoint1.stop();
  }

  // Returns true when the command should end.
  @Override public boolean isFinished() {
    return false;
  }
}
