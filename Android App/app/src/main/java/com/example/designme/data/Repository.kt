package com.example.designme.data

import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


@ViewModelScoped
class Repository @Inject constructor(
    remoteDataSource: RemoteDataSource,
   localDataSource: localDataSource
) {

    val remote = remoteDataSource
    val local = localDataSource

}