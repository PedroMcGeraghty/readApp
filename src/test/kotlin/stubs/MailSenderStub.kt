package stubs

import ar.edu.unsam.algo2.readapp.Mail
import ar.edu.unsam.algo2.readapp.MailSender

class MailSenderStub : MailSender {
    val mailsEnviados: MutableList<Mail> = mutableListOf()

    override fun enviarMail(mail: Mail) {
        mailsEnviados.add(mail)
    }

    fun filtrarEmailDestino(email: String): List<Mail> {
        return mailsEnviados.filter { it.to == email }
    }
}