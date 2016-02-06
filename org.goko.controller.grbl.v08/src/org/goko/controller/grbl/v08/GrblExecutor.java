package org.goko.controller.grbl.v08;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.goko.core.common.exception.GkException;
import org.goko.core.execution.monitor.executor.AbstractStreamingExecutor;
import org.goko.core.gcode.element.GCodeLine;
import org.goko.core.gcode.element.IGCodeProvider;
import org.goko.core.gcode.execution.ExecutionToken;
import org.goko.core.gcode.execution.ExecutionTokenState;
import org.goko.core.gcode.execution.IExecutor;
import org.goko.core.gcode.rs274ngcv3.IRS274NGCService;

public class GrblExecutor extends AbstractStreamingExecutor<ExecutionTokenState, ExecutionToken<ExecutionTokenState>> implements IExecutor<ExecutionTokenState, ExecutionToken<ExecutionTokenState>> {
	/** The underlying grbl service */
	private IGrblControllerService grblService;
	/** RS274 GCode service */
	private IRS274NGCService gcodeService;

	public GrblExecutor(IGrblControllerService grblService, IRS274NGCService gcodeService) {
		this.grblService = grblService;
		this.gcodeService = gcodeService;
	}
	/** (inheritDoc)
	 * @see org.goko.core.gcode.execution.IExecutor#createToken(org.goko.core.gcode.element.IGCodeProvider)
	 */
	@Override
	public ExecutionToken<ExecutionTokenState> createToken(IGCodeProvider provider) throws GkException {
		return new ExecutionToken<ExecutionTokenState>(gcodeService, provider, ExecutionTokenState.NONE);
	}

	/** (inheritDoc)
	 * @see org.goko.core.execution.monitor.executor.AbstractStreamingExecutor#send(org.goko.core.gcode.element.GCodeLine)
	 */
	@Override
	protected void send(GCodeLine line) throws GkException {
		grblService.send(line);
		getToken().setLineState(line.getId(), ExecutionTokenState.SENT);
	}

	/** (inheritDoc)
	 * @see org.goko.core.execution.monitor.executor.AbstractStreamingExecutor#isReadyForNextLine()
	 */
	@Override
	protected boolean isReadyForNextLine() throws GkException {
		if(getToken().hasMoreLine()){
			GCodeLine nextLine = getToken().getNextLine();
			String lineStr = gcodeService.render(nextLine);
			return grblService.getUsedGrblBuffer() + StringUtils.length(lineStr) < Grbl.GRBL_BUFFER_SIZE;
		}
		return true;
	}

	/** (inheritDoc)
	 * @see org.goko.core.gcode.execution.IExecutor#isReadyForQueueExecution()
	 */
	@Override
	public boolean isReadyForQueueExecution() throws GkException {
		return true;
	}
	/**
	 * Notification method called when a line is throwing error by Grbl
	 * @throws GkException
	 */
	protected GCodeLine markNextLineAsError() throws GkException{
		List<GCodeLine> lstLines = getToken().getLineByState(ExecutionTokenState.SENT);
		GCodeLine line = null;
		if(CollectionUtils.isNotEmpty(lstLines)){
			line = lstLines.get(0);
			getToken().setLineState(line.getId(), ExecutionTokenState.ERROR);
			getExecutionService().notifyCommandStateChanged(getToken(), line.getId());
		}
		return line;
	}

	/**
	 * Notification method called when a line is executed
	 * @throws GkException
	 */
	protected void confirmNextLineExecution() throws GkException{
		List<GCodeLine> lstLines = getToken().getLineByState(ExecutionTokenState.SENT);
		if(CollectionUtils.isNotEmpty(lstLines)){
			GCodeLine line = null;
			if(CollectionUtils.isNotEmpty(lstLines)){
				line = lstLines.get(0);
				getToken().setLineState(line.getId(), ExecutionTokenState.EXECUTED);
				getExecutionService().notifyCommandStateChanged(getToken(), line.getId());
			}
			notifyReadyForNextLineIfRequired();
			notifyTokenCompleteIfRequired();		
		}
	}

	/**
	 * Notify the parent executor if the conditions are met
	 * @throws GkException GkException
	 */
	protected void notifyReadyForNextLineIfRequired() throws GkException{
		if(isReadyForNextLine()){
			notifyReadyForNextLine();
		}
	}

	/**
	 * Notify the parent executor if the conditions are met
	 * @throws GkException GkException
	 */
	private void notifyTokenCompleteIfRequired() throws GkException {
		if(getToken().getLineCountByState(ExecutionTokenState.SENT) == 0){
			notifyTokenComplete();
		}
	}
	
	/** (inheritDoc)
	 * @see org.goko.core.execution.monitor.executor.AbstractStreamingExecutor#pause()
	 */
	@Override
	public void pause() throws GkException {		
		super.pause();
		grblService.pauseMotion();
	}
	
	/** (inheritDoc)
	 * @see org.goko.core.execution.monitor.executor.AbstractStreamingExecutor#stop()
	 */
	@Override
	public void stop() throws GkException {		
		super.stop();
		grblService.stopMotion();
	}

	/** (inheritDoc)
	 * @see org.goko.core.execution.monitor.executor.AbstractStreamingExecutor#start()
	 */
	@Override
	public void start() throws GkException {		
		super.start();
		grblService.startMotion();
	}
	
	/** (inheritDoc)
	 * @see org.goko.core.execution.monitor.executor.AbstractStreamingExecutor#resume()
	 */
	@Override
	public void resume() throws GkException {
		super.resume();
		grblService.resumeMotion();
	}
}
