import { element, by, ElementFinder } from 'protractor';

export default class ServerUpdatePage {
  pageTitle: ElementFinder = element(by.id('platformApp.server.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  serverIdInput: ElementFinder = element(by.css('input#server-serverId'));
  serverModelInput: ElementFinder = element(by.css('input#server-serverModel'));
  serverManufacturerInput: ElementFinder = element(by.css('input#server-serverManufacturer'));
  tagSelect: ElementFinder = element(by.css('select#server-tag'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setServerIdInput(serverId) {
    await this.serverIdInput.sendKeys(serverId);
  }

  async getServerIdInput() {
    return this.serverIdInput.getAttribute('value');
  }

  async setServerModelInput(serverModel) {
    await this.serverModelInput.sendKeys(serverModel);
  }

  async getServerModelInput() {
    return this.serverModelInput.getAttribute('value');
  }

  async setServerManufacturerInput(serverManufacturer) {
    await this.serverManufacturerInput.sendKeys(serverManufacturer);
  }

  async getServerManufacturerInput() {
    return this.serverManufacturerInput.getAttribute('value');
  }

  async tagSelectLastOption() {
    await this.tagSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async tagSelectOption(option) {
    await this.tagSelect.sendKeys(option);
  }

  getTagSelect() {
    return this.tagSelect;
  }

  async getTagSelectedOption() {
    return this.tagSelect.element(by.css('option:checked')).getText();
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
