package ru.lizzzi.crossfit_rekord.interfaces;

import com.backendless.exceptions.BackendlessFault;

public interface BackendlessResponseCallback<T> {

    void handleSuccess(T response);

    void handleFault(BackendlessFault fault);

}