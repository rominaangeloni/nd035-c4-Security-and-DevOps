const inputWrapperClassName = 'wallapop-input__wrapper';
const inputLabelClassName = 'wallapop-input__label';
const inputElementClassName = 'wallapop-input__element';

const handleInputFocusBlur = (inputTargetElement) => {
  const [wrapper, label] = findInputRelatedElements(inputTargetElement);
  toggleElementClassName(wrapper, `${inputWrapperClassName}--focused`);
  toggleElementClassName(label, `${inputLabelClassName}--focused`);
};

const handleInputChange = (inputTargetElement) => {
  const [wrapper, label] = findInputRelatedElements(inputTargetElement);
  const hasValue = !!inputTargetElement.value;
  toggleElementClassNameByOption(
    wrapper,
    `${inputWrapperClassName}--filled`,
    hasValue
  );
  toggleElementClassNameByOption(
    label,
    `${inputLabelClassName}--filled`,
    hasValue
  );
  toggleElementClassNameByOption(
    inputTargetElement,
    `${inputElementClassName}--filled`,
    hasValue
  );
};

const toggleElementClassName = (element, className) => {
  element?.classList.toggle(className);
};

const toggleElementClassNameByOption = (
  element,
  className,
  shouldAddClassName = false
) => {
  element?.classList?.[shouldAddClassName ? 'add' : 'remove'](className);
};

const findInputRelatedElements = (inputElement) => {
  const wrapper = inputElement.closest(`.${inputWrapperClassName}`);
  const label = wrapper.querySelector(`.${inputLabelClassName}`);
  return [wrapper, label];
};

document.addEventListener('DOMContentLoaded', () => {
  const inputElement = document.querySelector('input[name="username"]');
  if (!inputElement) return;
  handleInputChange(inputElement);
});
