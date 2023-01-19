package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.IntakeSubsystem;

public class OuttakeGamePieceCommand extends CommandBase {
    private final IntakeSubsystem m_intake;
    public OuttakeGamePieceCommand(IntakeSubsystem outtake) {
      m_intake = outtake;
      // Use addRequirements() here to declare subsystem dependencies.
      addRequirements(m_intake);
    }
  
    // Called when the command is initially scheduled.
    @Override public void initialize() {}
  
    // Called every time the scheduler runs while the command is scheduled.
    @Override public void execute() {
      m_intake.outward();
    }
  
    // Called once the command ends or is interrupted.
    @Override public void end(boolean interrupted) {
      m_intake.stop();
    }
  
    // Returns true when the command should end.
    @Override public boolean isFinished() {
      return false;
    }
  }    

