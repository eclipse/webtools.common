package org.eclipse.wst.validation;

/**
 * The combined results of validating multiple resources.
 * @author karasiuk
 *
 */
public class ValidationResults {
	private ValidationResult _result;
	
	public ValidationResults(ValidationResult result){
		_result = result;
	}
	
	/**
	 * Answer any validation messages that were added by the validation operation.
	 * @return an array is returned even if there are no messages.
	 */
	public ValidatorMessage[] getMessages(){
		if (_result == null)return new ValidatorMessage[0];
		return _result.getMessages();
	}

	/**
	 * Answer the number of error messages that were generated as part of this validation operation.
	 */
	public int getSeverityError() {
		if (_result == null)return 0;
		return _result.getSeverityError();
	}

	/**
	 * Answer the number of informational messages that were generated as part of this validation operation.
	 */
	public int getSeverityInfo() {
		if (_result == null)return 0;
		return _result.getSeverityInfo();
	}
	
	/**
	 * Answer the number of warning messages that were generated as part of this validation operation.
	 */
	public int getSeverityWarning() {
		if (_result == null)return 0;
		return _result.getSeverityWarning();
	}

}