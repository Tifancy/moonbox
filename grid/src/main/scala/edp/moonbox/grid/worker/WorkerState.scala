package edp.moonbox.grid.worker

import akka.actor.ActorRef


/**
  * Created by edp on 9/8/17.
  */
case class WorkerState(actor: ActorRef,
                       jobCount: Int,
                       maxCores: Int = 0,
                       freeCores: Int = 0,
                       maxMemory: Float = 0,
                       freeMemory: Float = 0,
                       globalCpuLoad: Float = 0,
                       globalFreeMemory: Float = 0,
                       ioLoad: Double = 0)
