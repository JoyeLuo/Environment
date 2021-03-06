/**
 */
package eu.cloudscaleproject.env.method.common.method;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Requirement</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link eu.cloudscaleproject.env.method.common.method.Requirement#getPosition <em>Position</em>}</li>
 *   <li>{@link eu.cloudscaleproject.env.method.common.method.Requirement#getResource <em>Resource</em>}</li>
 * </ul>
 * </p>
 *
 * @see eu.cloudscaleproject.env.method.common.method.MethodPackage#getRequirement()
 * @model
 * @generated
 */
public interface Requirement extends StatusNode {
	/**
	 * Returns the value of the '<em><b>Position</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Position</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Position</em>' attribute.
	 * @see #setPosition(int)
	 * @see eu.cloudscaleproject.env.method.common.method.MethodPackage#getRequirement_Position()
	 * @model default="0" required="true"
	 * @generated
	 */
	int getPosition();

	/**
	 * Sets the value of the '{@link eu.cloudscaleproject.env.method.common.method.Requirement#getPosition <em>Position</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Position</em>' attribute.
	 * @see #getPosition()
	 * @generated
	 */
	void setPosition(int value);

	/**
	 * Returns the value of the '<em><b>Resource</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Resource</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Resource</em>' attribute.
	 * @see #setResource(Object)
	 * @see eu.cloudscaleproject.env.method.common.method.MethodPackage#getRequirement_Resource()
	 * @model transient="true"
	 * @generated
	 */
	Object getResource();

	/**
	 * Sets the value of the '{@link eu.cloudscaleproject.env.method.common.method.Requirement#getResource <em>Resource</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Resource</em>' attribute.
	 * @see #getResource()
	 * @generated
	 */
	void setResource(Object value);

} // Requirement
