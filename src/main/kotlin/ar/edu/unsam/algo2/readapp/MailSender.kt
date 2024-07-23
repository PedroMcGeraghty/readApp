package ar.edu.unsam.algo2.readapp

interface MailSender {
    fun enviarMail(mail: Mail)
}

data class Mail(val from: String, val to: String, val asunto: String, val cuerpo: String)
