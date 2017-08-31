/* This function sends emails using the Editable Email Notification */
def call(LinkedHashMap <String, String> emailOptions){
  stage('Sending Email') {
    emailext(
      to: emailOptions['to'],
      subject: emailOptions['subject'],
      body: emailOptions['body']
    )
  }
}
