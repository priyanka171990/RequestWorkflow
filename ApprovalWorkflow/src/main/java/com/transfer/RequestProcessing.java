package com.transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * RequestProcessing class is use to store request and its list of approvers.
 *
 */
public class RequestProcessing {
	String req;
	Map<String, Function<String, Boolean>> approverCache;

	public String getReq() {
		return req;
	}

	public void setReq(String req) {
		this.req = req;
	}

	public Map<String, Function<String, Boolean>> getApproverCache() {
		return approverCache;
	}

	public void setApproverCache(
			Map<String, Function<String, Boolean>> approverCache) {
		this.approverCache = approverCache;
	}

/**
 * @param ArrayList takes list of approvers at a stage.
 * <p> Methods process the request in separate threads. 
 */
	public List<Future<Boolean>> process(ArrayList<String> al)
	{
		ExecutorService executorService = Executors.newCachedThreadPool();
		List<Future<Boolean>> ls =new ArrayList<Future<Boolean>>();
		for( String s:al)
		{   
			ls.add (executorService.submit(
					new Callable<Boolean>(){
						public Boolean call() throws Exception {
							return approverCache.get(s).apply(req);

						}
					}));

		}
		executorService.shutdown();
		return ls;
	}
}
