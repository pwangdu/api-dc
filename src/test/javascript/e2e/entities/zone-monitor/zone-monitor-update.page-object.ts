import { element, by, ElementFinder } from 'protractor';

export default class ZoneMonitorUpdatePage {
  pageTitle: ElementFinder = element(by.id('platformApp.zoneMonitor.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  zoneMonitorIdInput: ElementFinder = element(by.css('input#zone-monitor-zoneMonitorId'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setZoneMonitorIdInput(zoneMonitorId) {
    await this.zoneMonitorIdInput.sendKeys(zoneMonitorId);
  }

  async getZoneMonitorIdInput() {
    return this.zoneMonitorIdInput.getAttribute('value');
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
