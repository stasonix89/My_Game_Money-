package com.themoneygame.investments.averageprice;

public enum CalculationMode {
    TARGET_AVERAGE,   // режим 1: хочу среднюю N, есть сумма X → на какой цене покупать
    NEW_AVERAGE       // режим 2: покупаю по цене P на сумму X → какая будет средняя
}
