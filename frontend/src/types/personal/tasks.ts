// src/types/personal/tasks.ts

// 🔹 Тип задачи (категория)
export type TaskTypeDto = {
    id: number;
    name: string;      // "Спорт", "Работа", "Здоровье"
};

// 🔹 Задача (то, что приходит с backend)
export type TaskDto = {
    id: number;
    taskType: string;   // имя типа задачи, например "Спорт"
    text: string;       // "Пробежка 5 км"
    completed: boolean; // true = выполнено (история), false = активная
    month: number;      // 1-12
    year: number;       // 2025
    date: string;       // "YYYY-MM-DD"
};

// 🔹 Тело запроса при создании / редактировании задачи
export type TaskRequest = {
    taskType: string;   // имя типа задачи: "Спорт"
    text: string;
    completed: boolean;
    month: number;
    year: number;
    date: string;       // "YYYY-MM-DD"
};
