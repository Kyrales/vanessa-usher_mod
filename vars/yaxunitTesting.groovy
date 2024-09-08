/*
 * Vanessa-Usher
 * Copyright (C) 2019-2022 SilverBulleters, LLC - All Rights Reserved.
 * Unauthorized copying of this file in any way is strictly prohibited.
 * Proprietary and confidential.
 */
import groovy.transform.Field
import org.silverbulleters.usher.config.PipelineConfiguration
import org.silverbulleters.usher.config.stage.yaxunitOptional
import org.silverbulleters.usher.state.PipelineState
import org.silverbulleters.usher.wrapper.VRunner

@Field
PipelineConfiguration config

@Field
PipelineState state

@Field
yaxunitOptional stageOptional

/**
 * Выполнить yaxunit тестирование
 * @param config конфигурация
 * @param state состояние конвейера
 */
void call(PipelineConfiguration config, yaxunitOptional stageOptional, PipelineState state) {
  this.config = config
  this.state = state
  this.stageOptional = stageOptional

  infobaseHelper.unpackInfobase(config: config, state: state)

  catchError(message: 'Ошибка во время выполнения yaxunit тестирования', buildResult: 'FAILURE', stageResult: 'FAILURE') {
    testing()
  }

  catchError(message: 'Ошибка во время архивации отчетов yaxunit о тестировании', buildResult: 'FAILURE', stageResult: 'FAILURE') {
    testResultsHelper.packTestResults(config, stageOptional, state.yaxunit)
  }

}

private def testing() {
  def auth = config.defaultInfobase.auth
  if (credentialHelper.authIsPresent(auth)) {
    withCredentials([usernamePassword(credentialsId: auth, usernameVariable: 'DBUSERNAME', passwordVariable: 'DBPASSWORD')]) {
      def credential = credentialHelper.getAuthString()
      yaxunitTesting(credential)
    }
  } else {
    yaxunitTesting()
  }
}

private yaxunitTesting(String credential = '') {
  def command = VRunner.runYaxunit(config, stageOptional)
  command = command.replace("%credentialID%", credential)
  cmdRun(command)
}