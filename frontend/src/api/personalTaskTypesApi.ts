// src/api/personalTaskTypesApi.ts
import axiosClient from "./axiosClient";
import type { TaskTypeDto } from "../types/personal/tasks";

export const personalTaskTypesApi = {
    /**
     * Получить все типы задач.
     */
    async getTaskTypes(): Promise<TaskTypeDto[]> {
        const response = await axiosClient.get<TaskTypeDto[]>("/api/tasks/types");
        return response.data;
    },

    /**
     * Создать тип задачи.
     */
    async createTaskType(name: string): Promise<TaskTypeDto> {
        const response = await axiosClient.post<TaskTypeDto>("/api/tasks/types", {
            name,
        });
        return response.data;
    },

    /**
     * Обновить тип задачи.
     */
    async updateTaskType(id: number, name: string): Promise<TaskTypeDto> {
        const response = await axiosClient.put<TaskTypeDto>(
            `/api/tasks/types/${id}`,
            { name }
        );
        return response.data;
    },

    /**
     * Удалить тип задачи.
     */
    async deleteTaskType(id: number): Promise<void> {
        await axiosClient.delete(`/api/tasks/types/${id}`);
    },
};
