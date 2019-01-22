import { element, by, ElementFinder } from 'protractor';

export default class TagUpdatePage {
  pageTitle: ElementFinder = element(by.id('platformApp.tag.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  tagIdInput: ElementFinder = element(by.css('input#tag-tagId'));
  remainingBatteryInput: ElementFinder = element(by.css('input#tag-remainingBattery'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setTagIdInput(tagId) {
    await this.tagIdInput.sendKeys(tagId);
  }

  async getTagIdInput() {
    return this.tagIdInput.getAttribute('value');
  }

  async setRemainingBatteryInput(remainingBattery) {
    await this.remainingBatteryInput.sendKeys(remainingBattery);
  }

  async getRemainingBatteryInput() {
    return this.remainingBatteryInput.getAttribute('value');
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
