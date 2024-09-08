/*
 * Vanessa-Usher
 * Copyright (C) 2019-2022 SilverBulleters, LLC - All Rights Reserved.
 * Unauthorized copying of this file in any way is strictly prohibited.
 * Proprietary and confidential.
 */
package org.silverbulleters.usher.config.stage

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyDescription

/**
 * Настройки этапа yaxunit
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class yaxunitOptional extends BaseOptional {
  @JsonPropertyDescription("Путь к каталогу выгрузки отчета в формате Allure. Например, `./out/yaxunitallure`")
  String allurePath = "./out/yaxunitallure"

  @JsonPropertyDescription("Путь к файлу настроек фреймворка тестирования ./tools/JSON/yaxunit.json")
  String yaxunitsettings = './tools/JSON/yaxunit.json'

  yaxunitOptional() {
    name = 'yaxunit'
    id = 'yaxunit'
    timeout = 100
  }

}
