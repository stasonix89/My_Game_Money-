// src/api/personalTasksApi.ts
import axiosClient from "./axiosClient";
import type { TaskDto, TaskRequest } from "../types/personal/tasks";

export const personalTasksApi = {
    /**
     * Получить ВСЕ задачи пользователя.
     * Backend отдаёт:
     *  - активные (completed = false)
     *  - выполненные (completed = true)
     */
    async getTasks(): Promise<TaskDto[]> {
        const response = await axiosClient.get<TaskDto[]>("/api/tasks");
        return response.data;
    },

    /**
     * Создать новую задачу.
     */
    async createTask(payload: TaskRequest): Promise<TaskDto> {
        const response = await axiosClient.post<TaskDto>("/api/tasks", payload);
        return response.data;
    },

    /**
     * Обновить задачу (тип, текст, дата, completed).
     */
    async updateTask(id: number, payload: TaskRequest): Promise<TaskDto> {
        const response = await axiosClient.put<TaskDto>(
            `/api/tasks/${id}`,
            payload
        );
        return response.data;
    },

    /**
     * Удалить задачу.
     */
    async deleteTask(id: number): Promise<void> {
        await axiosClient.delete(`/api/tasks/${id}`);
    },
};
