/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.seata.web;

import io.seata.core.exception.TransactionException;
import io.seata.tm.api.DefaultFailureHandlerImpl;
import io.seata.tm.api.GlobalTransaction;

import org.springframework.stereotype.Component;

/**
 * @author kwings6
 */
@Component("failureHandler")
public class SeataTransationHandler extends DefaultFailureHandlerImpl {


	@Override
	public void onBeginFailure(GlobalTransaction tx, Throwable cause) {
		System.out.println("1");
		System.out.println(tx.getXid());
		try {
			System.out.println(tx.getStatus());
		}
		catch (TransactionException e) {
			throw new RuntimeException(e);
		}
		System.out.println(cause.getMessage());
		super.onBeginFailure(tx, cause);
	}

	@Override
	public void onCommitFailure(GlobalTransaction tx, Throwable cause) {
		System.out.println("2");
		System.out.println(tx.getXid());
		try {
			System.out.println(tx.getStatus());
		}
		catch (TransactionException e) {
			throw new RuntimeException(e);
		}
		System.out.println(cause.getMessage());
		super.onCommitFailure(tx, cause);
	}

	@Override
	public void onRollbackFailure(GlobalTransaction tx, Throwable originalException) {
		System.out.println("3");
		System.out.println(tx.getXid());
		try {
			System.out.println(tx.getStatus());
		}
		catch (TransactionException e) {
			throw new RuntimeException(e);
		}
		System.out.println(originalException.getMessage());
		super.onRollbackFailure(tx, originalException);
	}

	@Override
	public void onRollbacking(GlobalTransaction tx, Throwable originalException) {
		System.out.println("4");
		System.out.println(tx.getXid());
		try {
			System.out.println(tx.getStatus());
		}
		catch (TransactionException e) {
			throw new RuntimeException(e);
		}
		System.out.println(originalException.getMessage());
		super.onRollbacking(tx, originalException);
	}
}
