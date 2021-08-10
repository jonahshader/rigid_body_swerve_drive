package jonahshader.systems.physics

// static friction = staticCoefficient * normal force (this is an approximation)
// dynamic friction = dynamicCoefficient * normal force
data class DrivingSurface(var staticCoefficient: Float, var kineticCoefficient: Float)

val tarmacSurface = DrivingSurface(1f, 0.7f)
var iceSurface = DrivingSurface(.3f, .15f)