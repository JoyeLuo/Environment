package eu.cloudscaleproject.env.analyser.validation;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.monitorrepository.MonitorRepository;
import org.palladiosimulator.servicelevelobjective.ServiceLevelObjective;
import org.palladiosimulator.servicelevelobjective.ServiceLevelObjectiveRepository;
import org.scaledl.usageevolution.Usage;
import org.scaledl.usageevolution.UsageEvolution;

import eu.cloudscaleproject.env.analyser.alternatives.ConfAlternative;
import eu.cloudscaleproject.env.analyser.alternatives.InputAlternative;
import eu.cloudscaleproject.env.common.notification.IResourceValidator;
import eu.cloudscaleproject.env.common.notification.IValidationStatus;
import eu.cloudscaleproject.env.common.notification.IValidationStatusProvider;
import eu.cloudscaleproject.env.common.notification.ValidationException;
import eu.cloudscaleproject.env.toolchain.CSTool;
import eu.cloudscaleproject.env.toolchain.ToolchainUtils;

public class ConfValidator implements IResourceValidator {
	
	private static final String ERR_MODEL_ERROR = "eu.cloudscaleproject.env.analyser.validation.ConfValidator.modelerror";
	private static final String ERR_MODEL_EMPTY = "eu.cloudscaleproject.env.analyser.validation.ConfValidator.modelempty";
	private static final String ERR_INPUT_NOT_SET = "eu.cloudscaleproject.env.analyser.validation.ConfValidator.inputnotset";

	@Override
	public String getID() {
		return CSTool.ANALYSER_CONF.getID();
	}
	
	public boolean validateModels(IProject project, ConfAlternative ca) throws CoreException, ValidationException{
		
		boolean exp = true;
		boolean mp = true;
		boolean pms = true;
		boolean slo = true;
		
		List<IResource> expFiles = ca.getSubResources(ToolchainUtils.KEY_FILE_EXPERIMENTS);
		List<IResource> mpFiles = ca.getSubResources(ToolchainUtils.KEY_FILE_MESURPOINTS);
		List<IResource> monitorFiles = ca.getSubResources(ToolchainUtils.KEY_FILE_MONITOR);
		List<IResource> sloFiles = ca.getSubResources(ToolchainUtils.KEY_FILE_SLO);
		
		ca.getSelfStatus().checkError("Experiments missing", !expFiles.isEmpty(), false, "Experiments model is missing!");
		ca.getSelfStatus().checkError("MeasureP missing", !mpFiles.isEmpty(), false, "Measuring points model is missing!");
		ca.getSelfStatus().checkError("Monitor missing", !monitorFiles.isEmpty(), false, "Monitor model is missing!");
		ca.getSelfStatus().checkError("SLO missing", !sloFiles.isEmpty(), false, "SLO model is missing!");
		
		if(expFiles.isEmpty()){exp = false;}
		if(mpFiles.isEmpty()){mp = false;}
		if(monitorFiles.isEmpty()){pms = false;}
		if(sloFiles.isEmpty()){slo = false;}

		for(IResource file : expFiles){
			exp &= validateModel(ca, (IFile)file);
		}
		for(IResource file : mpFiles){
			mp &= validateModel(ca, (IFile)file);
		}
		for(IResource file : monitorFiles){
			pms &= validateModel(ca, (IFile)file);
		}
		for(IResource file : sloFiles){
			pms &= validateModel(ca, (IFile)file);
		}

		return mp && pms && slo && exp;
	}
	
	public boolean validateModel(ConfAlternative alt, IFile file) throws CoreException, ValidationException{
		
		IValidationStatus status  = alt.getStatus(file);
		
		if(file == null || !file.exists()){
			status.clearWarnings();
			status.setIsValid(false);
			return false;
		}
		else{
			URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
			Resource emfRes = alt.getResourceSet().getResource(uri, true);
			return validateModel(status, emfRes);
		}		
	}
	
	public boolean validateModel(IValidationStatus status, Resource resource) throws ValidationException{		
		// validate model
		
		if(status == null){
			return true;
		}
		
		status.checkError(ERR_MODEL_EMPTY, !resource.getContents().isEmpty(), false, "Model is empty");
		
		if(resource.getContents().isEmpty()){
			status.setIsValid(false);
			return false;
		}

		EObject eObject = resource.getContents().get(0);
		Diagnostic diagnostic = Diagnostician.INSTANCE.validate(eObject);
		boolean modelValid = diagnostic.getSeverity() == Diagnostic.OK;
		
		String message = String.format("Model validation failed : %s", 
				eObject.eClass().getName());

		status.checkError(ERR_MODEL_ERROR, modelValid, false, message);
		
		if(modelValid){
			status.setIsValid(true);
		}
		
		return modelValid;
	}
	
	private boolean validateExperiment(ConfAlternative alt) throws ValidationException{
		
		Experiment experiment = alt.getActiveExperiment();
	
		IValidationStatus status = alt.getSelfStatus();
		
		status.checkError("ExperimentError", experiment != null, true, "Experiment: Experiment does not exist!");
		status.checkError("InitialModelError", experiment.getInitialModel() != null, true, "Experiment: Initial model does not exist!");
		status.checkError("StopConditionError", !experiment.getStopConditions().isEmpty(), true, "Experiment: Stop condition is empty!");

		return true;
	}
	
	private boolean validateSloRepository(ConfAlternative alt) throws ValidationException{
		
		ServiceLevelObjectiveRepository sloRep = alt.getActiveSLORepository();
		IValidationStatus[] statusArray = alt.getSubStatus(ToolchainUtils.KEY_FILE_SLO);
		
		for(IValidationStatus status : statusArray){
			
			if(ConfAlternative.Type.CAPACITY.equals(alt.getTypeEnum())
					|| ConfAlternative.Type.SCALABILITY.equals(alt.getTypeEnum())){
				
				status.checkError("SLOError", sloRep != null , true, "This experiment type needs SLO specifications");
				status.checkError("SLOEmptyError", !sloRep.getServicelevelobjectives().isEmpty() , true, "This experiment type needs SLO specifications");

			}
			
			if(sloRep != null && !sloRep.getServicelevelobjectives().isEmpty()){
				
				String errorID = "SLOError";
				
				for(ServiceLevelObjective slo : sloRep.getServicelevelobjectives()){
					status.checkError(errorID, slo.getLowerThreshold() != null || slo.getUpperThreshold() != null, true,
							"SLO: '"+ slo.getName() +"' needs uper or lower threshold!");
					status.checkError(errorID, slo.getMeasurementSpecification() != null, true, 
							"SLO: '"+ slo.getName() +"' needs measurement specification!");
				}
			}
			status.clearWarnings();
		}

		return true;
	}
	
	private boolean validateMonitorRepository(ConfAlternative alt) throws ValidationException{

		String errorSpecMissingID = "MonitorSpecError";
		String errorSpecIncompleteID = "MonitorSpecIncompleteError";

		String errorMpID = "MonitorMpError";
		String errorMonitorRepID = "MonitorRepMissing";
		
		boolean isValid = true;
		
		MonitorRepository monitorRep = alt.getActiveMonitorRepository();
		IValidationStatus[] statusArray = alt.getSubStatus(ToolchainUtils.KEY_FILE_MONITOR);
		
		for(IValidationStatus status : statusArray){
			
			status.clearWarnings();
			status.checkError(errorMonitorRepID, monitorRep != null, true,
					"Monitor repository does not exist!");
			
			for(Monitor monitor : monitorRep.getMonitors()){
				
				try{
					status.checkError(errorSpecMissingID, monitor.getMeasurementSpecifications() != null, true,
						"Monitor: '"+ monitor.getEntityName() +"' needs measurement specification!");
				
					for(MeasurementSpecification ms : monitor.getMeasurementSpecifications()){
						status.checkError(errorSpecIncompleteID, ms.getMetricDescription() != null, true,
								"Monitor: '"+ monitor.getEntityName() +"' incomplete measurement specification!");
						status.checkError(errorSpecIncompleteID, ms.getStatisticalCharacterization() != null, true,
								"Monitor: '"+ monitor.getEntityName() +"' incomplete measurement specification!");
					}
				}
				catch(ValidationException e){
					isValid = false;
				}
				
				status.checkError(errorMpID, monitor.getMeasuringPoint() != null, true,
						"Monitor: '"+ monitor.getEntityName() +"' does not have measuring point specified!");
			}
		}

		return isValid;
	}
	
	private boolean validateUsageEvolution(ConfAlternative alt) throws ValidationException{
		
		UsageEvolution usageEvolution = alt.getActiveUsageEvolutionModel();
		if(usageEvolution == null){
			return true;
		}
		
		IValidationStatus[] statusArray = alt.getSubStatus(ToolchainUtils.KEY_FILE_USAGEEVOLUTION);
		
		for(IValidationStatus status : statusArray){
				
			status.checkError("UEEmpty", !usageEvolution.getUsages().isEmpty(), true, 
					"Usage evolution with empty configuration is set in Experiment!");
			
			for(Usage usage : usageEvolution.getUsages()){
				status.checkError("", usage.getLoadEvolution() != null, true, 
						"Usage evolution: '"+ usage.getEntityName() + "' does not have load evolution!");
				status.checkError("", usage.getScenario() != null, true, 
						"Usage evolution: '"+ usage.getEntityName() + "' does not have usage scenarion!");
			}
			status.clearWarnings();
		}
		return true;
	}
	
	@Override
	public void validate(IProject project, IValidationStatusProvider statusProvider) {
		
		ConfAlternative alternative = (ConfAlternative)statusProvider;
		IValidationStatus status = statusProvider.getSelfStatus();
		
		InputAlternative inputAlternative = alternative.getInputAlternative();
		
		try {
			status.checkError(ERR_INPUT_NOT_SET, inputAlternative != null, true, "Input alternative is not set!");
			inputAlternative.validate();

			boolean valid = validateModels(alternative.getProject(), alternative);

			//additional custom constrains 
			if(valid){
				try{valid &= validateExperiment(alternative);}
				catch(ValidationException e){ valid = false;};
				try{valid &= validateMonitorRepository(alternative);}
				catch(ValidationException e){ valid = false;};
				try{valid &= validateSloRepository(alternative);}
				catch(ValidationException e){ valid = false;};
				try{valid &= validateUsageEvolution(alternative);}
				catch(ValidationException e){ valid = false;};
			}
			
			//TODO: check usage evolution
			
			status.setIsValid(valid);
		} 
		catch (CoreException e) {
			e.printStackTrace();
		}
		catch (ValidationException e) {
			status.setIsValid(false);
		}
		
		status.setIsDirty(alternative.isDirty());
	}

}
