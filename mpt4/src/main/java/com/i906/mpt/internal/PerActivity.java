package com.i906.mpt.internal;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * @author Noorzaini Ilhami
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerActivity {
}
