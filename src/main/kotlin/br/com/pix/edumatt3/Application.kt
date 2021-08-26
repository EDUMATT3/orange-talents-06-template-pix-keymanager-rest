package br.com.pix.edumatt3

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.pix.edumatt3")
		.start()
}

