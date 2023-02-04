/*
 * Vanessa-Usher
 * Copyright (C) 2019-2022 SilverBulleters, LLC - All Rights Reserved.
 * Unauthorized copying of this file in any way is strictly prohibited.
 * Proprietary and confidential.
 */
import groovy.transform.Field
import org.silverbulleters.usher.config.PipelineConfiguration
import org.silverbulleters.usher.config.additional.ExtensionSource
import org.silverbulleters.usher.config.stage.PrepareBaseOptional
import org.silverbulleters.usher.state.PipelineState
import org.silverbulleters.usher.wrapper.VRunner

@Field
PipelineConfiguration config

@Field
PipelineState state

@Field
PrepareBaseOptional stageOptional

/**
 * Подготовить информационную базы
 * @param config конфигурация
 * @param state состояние конвейера
 */
void call(PipelineConfiguration config, PrepareBaseOptional stageOptional, PipelineState state) {
  this.config = config
  this.state = state
  this.stageOptional = stageOptional

  prepareBase()

  infobaseHelper.packInfobase(state)

}

private void prepareBase() {
  def auth = config.defaultInfobase.auth
  if (credentialHelper.authIsPresent(auth)) {
    withCredentials([usernamePassword(credentialsId: auth, usernameVariable: 'DBUSERNAME', passwordVariable: 'DBPASSWORD')]) {
      def credential = credentialHelper.getAuthString() // ??
      prepareBaseInternal(credential)
    }
  } else {
    prepareBaseInternal()
  }
}

private void prepareBaseInternal(String credential = '') {
  
  // *Каратаев Олег
  if (stageOptional.OK_JustRun == true) {
    migrate(credential)

    if (stageOptional.OK_PushEpf == true) {
      // Декомпилируем обработки из внешнего пути в папку проекта
      def command = [
      "vrunner",
      "decompileepf",
      stageOptional.OK_OutEpfPath,
      stageOptional.OK_InExt_epf
      ].join(" ")
      
      cmdRun(command)      
      
      // Пушим декомпилированные обработки на ГитЛаб
      def command1 = "git add ."
      cmdRun(command1)

      def command2 = "git commit -m \"External Data new\" | exit 0"         
      cmdRun(command2)
      
      def command3 = "git push"
      cmdRun(command3)
    }
    return
  }
  // *

  if (stageOptional.template.isEmpty()) {
    if (stageOptional.repo.isEmpty()) {
      loadRepo(credential)
      updateDB(credential)
    } else {
       // *Каратаев Олег - Загружаем конфигурацию в уже существующую базу, которую предварительно создали ранее.
      compile(credential)  // сборка конфигурации из текстовых файлов и загрузка конфигурации
      updateDB(credential) // обновление конфигурации.   
      //initDevFromSource(credential)
      // *
    }
  } else {
    initDevWithTemplate(credential)
    compile(credential)
    updateDB(credential)
  }

  stageOptional.extensions.each { source ->
    if (!source.name.empty || !source.sourcePath.empty) {
      compileExt(source, credential)
    }
  }

  migrate(credential)
}

private void loadRepo(credential) {
  def repoVersion = common.getRepoVersion(stageOptional.sourcePath)
  def command = VRunner.loadRepo(config, stageOptional, repoVersion)
  command = command.replace("%credentialID%", credential)
  auth = stageOptional.getRepo().getAuth()
  // fixme: если кред пустой - ругаться
  withCredentials([usernamePassword(credentialsId: auth, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
    def credentialRepo = credentialHelper.getAuthRepoString()
    command = command.replace("%credentialStorageID%", credentialRepo)
    cmdRun(command)
  }
}

private void updateDB(credential) {
  def command = VRunner.updateDB(config, stageOptional)
  command = command.replace("%credentialID%", credential)
  cmdRun(command)
}

private void initDevWithTemplate(credential) {
  def command = VRunner.initDevWithTemplate(config, stageOptional)
  command = command.replace("%credentialID%", credential)
  cmdRun(command)
}

private void initDevFromSource(credential) {
  def command = VRunner.initDevFromSource(config, stageOptional)
  command = command.replace("%credentialID%", credential)
  cmdRun(command)
}

private void compile(credential) {
  def command = VRunner.compile(config, stageOptional)
  command = command.replace("%credentialID%", credential)
  cmdRun(command)
}

private void compileExt(ExtensionSource source, String credential) {
  def command = VRunner.compileExt(config, source)
  command = command.replace("%credentialID%", credential)
  cmdRun(command)
}

private void migrate(credential) {
  def command = VRunner.migrate(config, stageOptional)
  command = command.replace("%credentialID%", credential)
  cmdRun(command)
}
