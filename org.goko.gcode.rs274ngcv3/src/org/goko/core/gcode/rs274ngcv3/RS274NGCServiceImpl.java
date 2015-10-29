package org.goko.core.gcode.rs274ngcv3;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.goko.core.common.exception.GkException;
import org.goko.core.common.exception.GkTechnicalException;
import org.goko.core.common.measure.SI;
import org.goko.core.common.measure.quantity.Quantity;
import org.goko.core.common.measure.quantity.Time;
import org.goko.core.common.measure.quantity.type.NumberQuantity;
import org.goko.core.common.utils.CacheById;
import org.goko.core.common.utils.SequentialIdGenerator;
import org.goko.core.gcode.element.GCodeLine;
import org.goko.core.gcode.element.GCodeWord;
import org.goko.core.gcode.element.IGCodeProvider;
import org.goko.core.gcode.element.IInstructionProvider;
import org.goko.core.gcode.rs274ngcv3.context.GCodeContext;
import org.goko.core.gcode.rs274ngcv3.element.GCodeProvider;
import org.goko.core.gcode.rs274ngcv3.element.GCodeProviderWithModifier;
import org.goko.core.gcode.rs274ngcv3.element.InstructionIterator;
import org.goko.core.gcode.rs274ngcv3.element.InstructionProvider;
import org.goko.core.gcode.rs274ngcv3.element.InstructionSet;
import org.goko.core.gcode.rs274ngcv3.element.InstructionType;
import org.goko.core.gcode.rs274ngcv3.instruction.AbstractInstruction;
import org.goko.core.gcode.rs274ngcv3.instruction.AbstractStraightInstruction;
import org.goko.core.gcode.rs274ngcv3.instruction.InstructionFactory;
import org.goko.core.gcode.rs274ngcv3.instruction.executiontime.InstructionTimeCalculatorFactory;
import org.goko.core.gcode.rs274ngcv3.modifier.TestModifier;
import org.goko.core.gcode.rs274ngcv3.parser.GCodeLexer;
import org.goko.core.gcode.rs274ngcv3.parser.GCodeToken;
import org.goko.core.gcode.rs274ngcv3.parser.GCodeTokenType;
import org.goko.core.gcode.rs274ngcv3.parser.ModalGroup;
import org.goko.core.log.GkLog;
import org.goko.core.math.BoundingTuple6b;
import org.goko.core.math.Tuple6b;

/**
 * @author Psyko
 */
public class RS274NGCServiceImpl implements IRS274NGCService{
	private static final GkLog LOG = GkLog.getLogger(RS274NGCServiceImpl.class);
	/** The list of modal groups */
	private List<ModalGroup> modalGroups;	
	/** The cache of providers */
	private CacheById<IGCodeProvider> cacheProviders;
	
	/** Constructor */
	public RS274NGCServiceImpl() {
		initializeModalGroups();
		this.cacheProviders = new CacheById<IGCodeProvider>(new SequentialIdGenerator());		
	}
	
	/** (inheritDoc)
	 * @see org.goko.core.common.service.IGokoService#getServiceId()
	 */
	@Override
	public String getServiceId() throws GkException {		
		return "org.goko.core.gcode.rs274ngcv3.RS274NGCServiceImpl";
	}
	
	/** (inheritDoc)
	 * @see org.goko.core.common.service.IGokoService#start()
	 */
	@Override
	public void start() throws GkException {
		LOG.info("Starting " + getServiceId());	
		
		LOG.info("Successfully started " + getServiceId());
	}
	
	/** (inheritDoc)
	 * @see org.goko.core.common.service.IGokoService#stop()
	 */
	@Override
	public void stop() throws GkException {
		LOG.info("Stopping " + getServiceId());	
		
		LOG.info("Successfully stopped " + getServiceId());
	}
	/** (inheritDoc)
	 * @see org.goko.core.gcode.rs274ngcv3.IGCodeService#parse(java.io.InputStream)
	 */
	@Override
	public IGCodeProvider parse(InputStream inputStream) throws GkException {
		GCodeProviderWithModifier provider = new GCodeProviderWithModifier();		
		GCodeLexer lexer = new GCodeLexer();
		List<List<GCodeToken>> tokens = lexer.tokenize(inputStream);
		int lineNumber = 1;
		
		for (List<GCodeToken> lstToken : tokens) {
			verifyModality(lstToken);
			GCodeLine line = buildLine(lstToken);
			line.setLineNumber(lineNumber);
			provider.addLine(line);
			lineNumber = lineNumber + 1;
		}
		provider.addModifier(new TestModifier());
		cacheProviders.add(provider);
		return provider;
	}
	
	/** (inheritDoc)
	 * @see org.goko.core.gcode.rs274ngcv3.IRS274NGCService#parse(java.lang.String)
	 */
	@Override
	public IGCodeProvider parse(String inputString) throws GkException {
		return parse(new ByteArrayInputStream(inputString.getBytes()));
	}
	
	/** (inheritDoc)
	 * @see org.goko.core.gcode.rs274ngcv3.IRS274NGCService#parseLine(java.lang.String)
	 */
	@Override
	public GCodeLine parseLine(String inputString) throws GkException {
		IGCodeProvider provider = parse(new ByteArrayInputStream(inputString.getBytes()));
		return provider.getLines().get(0);
	}
	
	/**
	 * Build a GCodeLine using a list of tokens 
	 * @param lstToken the list of tokens to use
	 * @return a GCodeLine
	 * @throws GkException GkException 
	 */
	private GCodeLine buildLine(List<GCodeToken> lstToken) throws GkException {
		GCodeLine line = new GCodeLine();
		
		for (GCodeToken token : lstToken) {			
//			if(token.getType() == GCodeTokenType.LINE_NUMBER){
//				line.setLineNumber(GCodeTokenUtils.getLineNumber(token));				
//			}else 
			if(token.getType() == GCodeTokenType.WORD || token.getType() == GCodeTokenType.LINE_NUMBER){
				line.addWord(new GCodeWord(StringUtils.substring(token.getValue(), 0, 1), StringUtils.substring(token.getValue(), 1)));
			}else if(token.getType() == GCodeTokenType.SIMPLE_COMMENT
					|| token.getType() == GCodeTokenType.MULTILINE_COMMENT){
				line.addWord(new GCodeWord(";", token.getValue()));
			}
		}
		return line;
	}


	/** (inheritDoc)
	 * @see org.goko.core.gcode.rs274ngcv3.IGCodeService#getInstructions(org.goko.core.gcode.rs274ngcv3.context.GCodeContext, org.goko.core.gcode.element.IGCodeProvider)
	 */
	@Override
	public InstructionProvider getInstructions(final GCodeContext context, IGCodeProvider gcodeProvider) throws GkException {
		GCodeContext localContext = null;
		if(context != null){
			localContext = new GCodeContext(context);
		}else{
			localContext = context;
		}
		
		InstructionFactory factory = new InstructionFactory();		
		InstructionProvider instructionProvider = new InstructionProvider();
		
		for (GCodeLine gCodeLine : gcodeProvider.getLines()) {
			InstructionSet iSet = new InstructionSet();
			List<GCodeWord> localWords = new ArrayList<GCodeWord>(gCodeLine.getWords());
			// A line can contain multiple instructions
			while(CollectionUtils.isNotEmpty(localWords)){
				int wordCountBefore = localWords.size();
				AbstractInstruction instruction = factory.build(localContext, localWords);	
				if(instruction == null){
					// We have words is the list, but we can't build any instruction from them. End while loop
					traceUnusedWords(localWords);
					break;
				}else{
					// Make sure we consummed at least one word
					if(localWords.size() == wordCountBefore){
						throw new GkTechnicalException("An instruction was created but no word was removed. Instruction created : "+instruction.getClass());
					}
				}
				
				instruction.setIdGCodeLine(gCodeLine.getId());
				iSet.addInstruction(instruction);
				// Update context for further instructions
				update(localContext, instruction);
			}
			instructionProvider.addInstructionSet(iSet);
		}
		
		return instructionProvider;
	}
	
	/** (inheritDoc)
	 * @see org.goko.core.gcode.rs274ngcv3.IRS274NGCService#getGCodeProvider(org.goko.core.gcode.rs274ngcv3.context.GCodeContext, org.goko.core.gcode.rs274ngcv3.element.InstructionProvider)
	 */
	@Override
	public GCodeProvider getGCodeProvider(GCodeContext context, InstructionProvider instructionProvider) throws GkException {
		InstructionFactory factory = new InstructionFactory();
		GCodeProvider provider = new GCodeProvider();
		
		List<InstructionSet> sets = instructionProvider.getInstructionSets();				
		for (InstructionSet instructionSet : sets) {
			GCodeLine line = factory.getLine(context, instructionSet);
			
			provider.addLine(line);
		}
		
		return provider;
	}
	
	/**
	 * Trace the unused words in a line 
	 * @param unusedWords the list of unused words
	 */
	private void traceUnusedWords(List<GCodeWord> unusedWords){
		String wordstr = "";
		for (GCodeWord gCodeWord : unusedWords) {
			wordstr += gCodeWord.completeString() + " ";
		}		
		LOG.warn("GCodeWord not supported "+wordstr+". They will be present in the GCode file, but won't generate instruction");
	}
	
	/**
	 * Verify modality for the given list of token
	 * @param lstToken the list of tokens to check 
	 * @throws GkException GkException if there is a modality violation
	 */
	private void verifyModality(List<GCodeToken> lstToken) throws GkException{
		for (ModalGroup group : modalGroups) {
			group.verifyModality(lstToken);
		}
	}
	
	/**
	 * Initialize the list of modal groups 
	 */
	protected void initializeModalGroups(){
		this.modalGroups = new ArrayList<ModalGroup>();
		this.modalGroups.add( new ModalGroup("G0", "G00", "G1", "G01", "G2", "G02", "G3", "G03", "G38.2", "G80", "G81", "G82", "G83", "G84", "G85", "G86", "G87", "G88", "G89" ));

		this.modalGroups.add( new ModalGroup("G17", "G18", "G19"  ));
		this.modalGroups.add( new ModalGroup("G90", "G91"));
		this.modalGroups.add( new ModalGroup("G93", "G94"));
		this.modalGroups.add( new ModalGroup("G20", "G21"));
		this.modalGroups.add( new ModalGroup("G40", "G41", "G42"));
		this.modalGroups.add( new ModalGroup("G43","G49"));
		this.modalGroups.add( new ModalGroup("G98","G99"));
		this.modalGroups.add( new ModalGroup("G54", "G55", "G56", "G57", "G58", "G59", "G59.1", "G59.2", "G59.3"));
		this.modalGroups.add( new ModalGroup("G61", "G61.1", "G64"));

		this.modalGroups.add( new ModalGroup("M0", "M1", "M2", "M30", "M60"));
		this.modalGroups.add( new ModalGroup("M6"));
		this.modalGroups.add( new ModalGroup("M3", "M03","M4", "M04", "M5", "M05"));
		this.modalGroups.add( new ModalGroup("M7", "M07", "M9", "M09"));
		this.modalGroups.add( new ModalGroup("M8", "M08", "M9", "M09"));
		this.modalGroups.add( new ModalGroup("M48", "M49"));
	}	
	
	public GCodeContext update(GCodeContext baseContext, AbstractInstruction instruction) throws GkException {
		instruction.apply(baseContext);
		return baseContext; 
	};
	
	
	/** (inheritDoc)
	 * @see org.goko.core.gcode.service.IGCodeService#update(org.goko.core.gcode.element.IGCodeContext, org.goko.core.gcode.element.IInstructionSet)
	 */
	@Override
	public GCodeContext update(GCodeContext baseContext, InstructionSet instructionSet) throws GkException {
		GCodeContext result = baseContext;
		List<AbstractInstruction> instructions = instructionSet.getInstructions();
		if(CollectionUtils.isNotEmpty(instructions)){
			for (AbstractInstruction instruction : instructions) {
				result = update(result, instruction);
			}
		}
		return result;
	}

	/** (inheritDoc)
	 * @see org.goko.core.gcode.service.IGCodeService#getIterator(org.goko.core.gcode.element.IInstructionProvider, org.goko.core.gcode.element.IGCodeContext)
	 */
	@Override
	public InstructionIterator getIterator(IInstructionProvider<AbstractInstruction, InstructionSet> instructionProvider, GCodeContext baseContext) throws GkException {		
		return new InstructionIterator(instructionProvider, new GCodeContext(baseContext), this);
	}

	/** (inheritDoc)
	 * @see org.goko.core.execution.IGCodeExecutionTimeService#evaluateExecutionTime(org.goko.core.gcode.element.IGCodeProvider)
	 */
	@Override
	public Quantity<Time> evaluateExecutionTime(IGCodeProvider provider, GCodeContext baseContext) throws GkException {
		Quantity<Time> result = NumberQuantity.zero(SI.SECOND);
		
		InstructionTimeCalculatorFactory timeFactory = new InstructionTimeCalculatorFactory();		
		InstructionProvider instructions = getInstructions(baseContext, provider);
		
		InstructionIterator iterator = getIterator(instructions, baseContext);
		
		GCodeContext preContext = null;	 
		
		while(iterator.hasNext()){
			preContext = new GCodeContext(iterator.getContext());
			result = result.add( timeFactory.getExecutionTime(preContext, iterator.next()) );
		}
		
		return result;
	}

	/** (inheritDoc)
	 * @see org.goko.core.gcode.service.IGCodeService#render(org.goko.core.gcode.element.GCodeLine)
	 */
	@Override
	public String render(GCodeLine line) throws GkException {
		StringBuffer buffer = new StringBuffer();
		// FIXME find a better way to classify GCode words or describe a GCodeLine within the rs274 service
		GCodeWord commentWord = null;
		
		// Add words
		for (GCodeWord word : line.getWords()) {
			if(StringUtils.equals(word.getLetter(), "N")){				
				buffer.insert(0, word.getValue());
				buffer.insert(0, word.getLetter());
				continue;
			}
			
			if(StringUtils.equals(word.getLetter(), ";")){				
				commentWord = word;
				continue;
			}
			
			if(buffer.length() > 0){
				buffer.append(" ");
			}
			buffer.append(word.getLetter());
			buffer.append(word.getValue());			
		}
		// Add comment
		if(commentWord != null){
			if(buffer.length() > 0){
				buffer.append(" ");
			}
			buffer.append(commentWord.getValue());
		}
		return buffer.toString();
	}

	/** (inheritDoc)
	 * @see org.goko.core.gcode.rs274ngcv3.IRS274NGCService#getBounds(org.goko.core.gcode.rs274ngcv3.context.GCodeContext, org.goko.core.gcode.rs274ngcv3.element.InstructionProvider)
	 */
	@Override
	public BoundingTuple6b getBounds(GCodeContext context, InstructionProvider instructionProvider) throws GkException {
		Tuple6b min = new Tuple6b();
		Tuple6b max = new Tuple6b();
		
		GCodeContext preContext = new GCodeContext(context);
		InstructionIterator iterator = getIterator(instructionProvider, preContext);
		
		while (iterator.hasNext()) {
			preContext = new GCodeContext(iterator.getContext());			
			AbstractInstruction instruction = iterator.next();
			
			if(instruction.getType() == InstructionType.STRAIGHT_TRAVERSE 
			|| instruction.getType() == InstructionType.STRAIGHT_FEED){
				AbstractStraightInstruction straightInstruction = (AbstractStraightInstruction) instruction;
				Tuple6b endpoint = new Tuple6b(straightInstruction.getX(),straightInstruction.getY(),straightInstruction.getZ(),straightInstruction.getA(),straightInstruction.getB(),straightInstruction.getC());
				min = min.min(endpoint);
				max = max.max(endpoint);						
			}
		}
		
		return new BoundingTuple6b(min, max);
	}
	
	/** (inheritDoc)
	 * @see org.goko.core.gcode.rs274ngcv3.IRS274NGCService#getGCodeProvider(java.lang.Integer)
	 */
	@Override
	public IGCodeProvider getGCodeProvider(Integer id) throws GkException {
		return cacheProviders.get(id);
	}
}