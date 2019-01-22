import { element, by, ElementFinder } from 'protractor';

export default class RackUpdatePage {
  pageTitle: ElementFinder = element(by.id('platformApp.rack.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  rackIdInput: ElementFinder = element(by.css('input#rack-rackId'));
  zoneMonitorSelect: ElementFinder = element(by.css('select#rack-zoneMonitor'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setRackIdInput(rackId) {
    await this.rackIdInput.sendKeys(rackId);
  }

  async getRackIdInput() {
    return this.rackIdInput.getAttribute('value');
  }

  async zoneMonitorSelectLastOption() {
    await this.zoneMonitorSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async zoneMonitorSelectOption(option) {
    await this.zoneMonitorSelect.sendKeys(option);
  }

  getZoneMonitorSelect() {
    return this.zoneMonitorSelect;
  }

  async getZoneMonitorSelectedOption() {
    return this.zoneMonitorSelect.element(by.css('option:checked')).getText();
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
