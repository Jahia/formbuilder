<a href="https://www.jahia.com/">
    <img src="https://www.jahia.com/modules/jahiacom-templates/images/jahia-3x.png" alt="Jahia logo" title="Jahia" align="right" height="60" />
</a>

Jahia Form Builder Module
======================

 The form builder allows you to create forms to interact with you visitors (contact, questions, surveys, support, etc.).

 The forms built using the form builder are fully customizable, they can have client side validation, captcha to ensure
 integrity of data. You can choose the actions you want to execute when the form is submited.
  
 ## Create a form step-by-step

  The first object to add in your page is a form object under the Form Components category.
  The Form object has three sub zones in edit mode :

 ### Action Zone

In this zone you must drop an action at least. If you create multiple actions, they will be executed in the order
there are created and the result sent back after the form submission will be the result of the last action executed.
For example, if you want to use the "save to the repository" action and a "redirect to page" action you have to
put them in this order : "save to the repository", "redirect to page".
You can switch them to change the order using drag&drop in Edit mode.

### Fieldset Zone

This zone contains only fieldsets, then inside the fieldset you have a subzone to drop your form elements
(input text, text area, choice lists).

In all those elements you have a sub zone for both validations and options when in a choicelist.

 ### Buttons Zone

In this area you must drop your buttons like submit button or reset button.

## Extends the form builder

 To extend the form builder you will have to add new definitions in the definitions.cnd file of the module.

### Add new actions

  To add a new action, first you have to code it in java and add it to Jahia (see how to develop a custom action).

  Then add a new definition extending <jnt:formAction>. This definition must have a property defined
  like that <- j:action (string) = 'mail' autocreated hidden> where you have to replace <mail> by the <key> value
  of your action defined inside the spring file of the module containing the action class.

  The user can now use you action to add it to his forms.

### Add new form elements

  This way you can define complex pieces of form or specialized simple elements. This form elements must extend
  <jnt:formElement>.

### Add new buttons

  This way you can define new button elements for your forms. This form buttons must extend <jnt:formButton>.

### Add new validation

  This way you can define new validation elements for your forms. This form validations must extend <jnt:formElementValidation>.
  The validation framework used is a jquery plugin, you can found the [documentation here](http://bassistance.de/jquery-plugins/jquery-plugin-validation/)

### Add new self populated choice list

  In the spring file of the form builder module you can extends the number of self populated choice lists available
  (by default only country and users are available). When declaring a self populated choice list to be available to the users
  in their form you have to declare the name of the list and the list of initializers it will use and the renderer if needed.

```xml
<bean id="formBuilderInitializer" class="org.jahia.modules.formbuilder.initializers.ChoiceListTypeInitializers">
    <property name="key" value="choicelisttypes"/>
    <property name="initializersMap">
        <map key-type="java.lang.String" value-type="java.lang.String">
            <entry key="country" value="country,flag;renderer=flagcountry"/>
            <entry key="users" value="users"/>
        </map>
    </property>
</bean>
```

## Open-Source

This is an Open-Source module, you can find more details about Open-Source @ Jahia [in this repository](https://github.com/Jahia/open-source).
