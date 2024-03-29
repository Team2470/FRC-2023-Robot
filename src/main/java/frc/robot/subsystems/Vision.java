// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Vision extends SubsystemBase {
  private final NetworkTable m_cameraTable = NetworkTableInstance.getDefault().getTable("CameraPublisher");
  private final NetworkTableEntry m_cameraSelector = m_cameraTable.getEntry("selector");
  /** Creates a new Vision. */
  public Vision() {
    showGrid();
  }

  public void showGrid() {
    m_cameraSelector.setDouble(0);
  }

  public void showGripper() {
    m_cameraSelector.setDouble(1);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
