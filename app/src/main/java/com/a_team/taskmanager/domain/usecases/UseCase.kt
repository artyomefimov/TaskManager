package com.a_team.taskmanager.domain.usecases

import io.reactivex.Single

interface UseCase<T> {
    fun execute(): Single<T>
}