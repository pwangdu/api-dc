import { element, by, ElementFinder } from 'protractor';

export default class DatapointUpdatePage {
  pageTitle: ElementFinder = element(by.id('platformApp.datapoint.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  tagInput: ElementFinder = element(by.css('input#datapoint-tag'));
  captureTimeInput: ElementFinder = element(by.css('input#datapoint-captureTime'));
  valueInput: ElementFinder = element(by.css('input#datapoint-value'));
  rackSelect: ElementFinder = element(by.css('select#datapoint-rack'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setTagInput(tag) {
    await this.tagInput.sendKeys(tag);
  }

  async getTagInput() {
    return this.tagInput.getAttribute('value');
  }

  async setCaptureTimeInput(captureTime) {
    await this.captureTimeInput.sendKeys(captureTime);
  }

  async getCaptureTimeInput() {
    return this.captureTimeInput.getAttribute('value');
  }

  async setValueInput(value) {
    await this.valueInput.sendKeys(value);
  }

  async getValueInput() {
    return this.valueInput.getAttribute('value');
  }

  async rackSelectLastOption() {
    await this.rackSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async rackSelectOption(option) {
    await this.rackSelect.sendKeys(option);
  }

  getRackSelect() {
    return this.rackSelect;
  }

  async getRackSelectedOption() {
    return this.rackSelect.element(by.css('option:checked')).getText();
  }

  async save() {
    await this.saveButton.click();
  }

  async cancel() {
    await this.cancelButton.click();
  }

  getSaveButton() {
    return this.saveButton;
  }
}
