package jonahshader.systems.pathing

//val playbackFun: CarControlFun = {env -> }

fun playbackFunctor(sequence: CarControlSequence) : CarControlFun = { env ->
    sequence.sequence[env.time]
}