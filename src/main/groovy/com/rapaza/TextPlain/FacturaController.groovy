package com.rapaza.TextPlain

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletResponse
import java.text.DateFormat
import java.text.Format
import java.text.SimpleDateFormat

@RestController
@RequestMapping(path = "/v1/", produces = MediaType.TEXT_PLAIN_VALUE)
class FacturaController {
    final String FORMATO_FECHA = 'dd/MM/yyyy'

    @GetMapping(value = 'status', consumes = MediaType.APPLICATION_JSON_VALUE)
    void status(HttpServletResponse response) throws IOException {
        response.addHeader("content-type", "text/plain; charset=utf-8")
        response.setStatus(200)

        PrintWriter out = response.getWriter()
        out.println("OK")
    }

    @GetMapping(value = "/facturas/{cantidad}", consumes = MediaType.APPLICATION_JSON_VALUE)
    void obtenerFacturas(HttpServletResponse response, @PathVariable Integer cantidad) {
        //println "cantidad: ${cantidad}"
        response.addHeader("content-type", "text/plain; charset=utf-8")
        PrintWriter out = response.getWriter()
        try {
            def textList = generarTextPlano(cantidad)
            //println textList.join('\n')
            response.addHeader("identificador-lote", UUID.randomUUID().toString().replaceAll('-', '').substring(0, 10))
            response.addHeader("cantidad", "${cantidad}")
            response.setStatus(200)

            out.println textList.join('\n')
        } catch (Exception e) {
            e.printStackTrace()
            response.setStatus(500)

            out.println "HTTP Error 500"
        }
    }

    @PutMapping(value = "/facturas/{cantidad}", consumes = MediaType.APPLICATION_JSON_VALUE)
    void confirmarRegistroFacturas(HttpServletResponse response, @RequestBody Map<String, String> params, @PathVariable Integer cantidad) {
        //println "cantidad: ${cantidad}"
        response.addHeader("content-type", "text/plain; charset=utf-8")
        PrintWriter out = response.getWriter()
        try {
            println params
            println " >>> Confirmar Facturas <<< "
            response.addHeader("cantidad", "${cantidad}")
            response.setStatus(200)

            out.println "OK"
        } catch (Exception e) {
            e.printStackTrace()
            response.setStatus(500)

            out.println "HTTP Error 500"
        }
    }

    private def generarTextPlano(Integer cantidad) {

        Format formatter = new SimpleDateFormat("dd/MM/yyyy")
        def rnd = new Random()

        def textList = []
        (1..cantidad).each {
            Calendar calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, rnd.nextInt(365) * -1)

            int factor = rnd.nextInt(4500)
            def importe = rnd.nextDouble() * factor

            def item = [
                it,
                Math.abs(rnd.nextInt() % 10000000000000 + 1), // NRO AUTORIZACION
                rnd.nextInt(1000000), //NIT
                rnd.nextInt(10000), //FACTURA
                formatter.format(calendar.getTime()), //FEHA
                Math.round(importe * 100.0) / 100.0,   //IMPORTE
                UUID.randomUUID().toString().substring(6, 26) //CODIGO DE CONTROLE
            ]
            textList << item.join('|')
        }

        return textList
    }

}
//