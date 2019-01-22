import { element, by, ElementFinder } from 'protractor';

export default class MembershipUpdatePage {
  pageTitle: ElementFinder = element(by.id('platformApp.membership.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  startTimeInput: ElementFinder = element(by.css('input#membership-startTime'));
  endTimeInput: ElementFinder = element(by.css('input#membership-endTime'));
  serverSelect: ElementFinder = element(by.css('select#membership-server'));
  rackSelect: ElementFinder = element(by.css('select#membership-rack'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setStartTimeInput(startTime) {
    await this.startTimeInput.sendKeys(startTime);
  }

  async getStartTimeInput() {
    return this.startTimeInput.getAttribute('value');
  }

  async setEndTimeInput(endTime) {
    await this.endTimeInput.sendKeys(endTime);
  }

  async getEndTimeInput() {
    return this.endTimeInput.getAttribute('value');
  }

  async serverSelectLastOption() {
    await this.serverSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async serverSelectOption(option) {
    await this.serverSelect.sendKeys(option);
  }

  getServerSelect() {
    return this.serverSelect;
  }

  async getServerSelectedOption() {
    return this.serverSelect.element(by.css('option:checked')).getText();
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
