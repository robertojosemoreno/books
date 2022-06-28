package yucatalks.micronaut

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("yucatalks.micronaut")
		.start()
}

