package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeSubsystem extends SubsystemBase {
    private final TalonFX m_motor;
    public IntakeSubsystem (){
        m_motor =new TalonFX (10);
        m_motor.setInverted(false);    
    }
    public void inward (){
        m_motor.set (ControlMode.PercentOutput, .5);
    }
    public void outward (){
        m_motor.set(ControlMode.PercentOutput, -.5);
    }
    public void stop (){
        m_motor.neutralOutput();
    }
    
  @Override public void periodic() {
    // once per scheduler run
  }
}



