package jonahshader.systems.physics

data class WheelParams(
    val wheelDiameter: Float, // meters i guess
    val motorMaxTorque: Float, // newton meters
    val motorMaxRPM: Float,
    val motorToWheelRatio: Float, // motor rotations per wheel rotation
)
