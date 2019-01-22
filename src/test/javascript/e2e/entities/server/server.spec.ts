/* tslint:disable no-unused-expression */
import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import ServerComponentsPage from './server.page-object';
import { ServerDeleteDialog } from './server.page-object';
import ServerUpdatePage from './server-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('Server e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let serverUpdatePage: ServerUpdatePage;
  let serverComponentsPage: ServerComponentsPage;
  let serverDeleteDialog: ServerDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();

    await signInPage.username.sendKeys('admin');
    await signInPage.password.sendKeys('admin');
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();

    await waitUntilDisplayed(navBarPage.entityMenu);
  });

  it('should load Servers', async () => {
    await navBarPage.getEntityPage('server');
    serverComponentsPage = new ServerComponentsPage();
    expect(await serverComponentsPage.getTitle().getText()).to.match(/Servers/);
  });

  it('should load create Server page', async () => {
    await serverComponentsPage.clickOnCreateButton();
    serverUpdatePage = new ServerUpdatePage();
    expect(await serverUpdatePage.getPageTitle().getAttribute('id')).to.match(/platformApp.server.home.createOrEditLabel/);
  });

  it('should create and save Servers', async () => {
    const nbButtonsBeforeCreate = await serverComponentsPage.countDeleteButtons();

    await serverUpdatePage.setServerIdInput('serverId');
    expect(await serverUpdatePage.getServerIdInput()).to.match(/serverId/);
    await serverUpdatePage.setServerModelInput('serverModel');
    expect(await serverUpdatePage.getServerModelInput()).to.match(/serverModel/);
    await serverUpdatePage.setServerManufacturerInput('serverManufacturer');
    expect(await serverUpdatePage.getServerManufacturerInput()).to.match(/serverManufacturer/);
    await serverUpdatePage.tagSelectLastOption();
    await waitUntilDisplayed(serverUpdatePage.getSaveButton());
    await serverUpdatePage.save();
    await waitUntilHidden(serverUpdatePage.getSaveButton());
    expect(await serverUpdatePage.getSaveButton().isPresent()).to.be.false;

    await serverComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
    expect(await serverComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
  });

  it('should delete last Server', async () => {
    await serverComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeDelete = await serverComponentsPage.countDeleteButtons();
    await serverComponentsPage.clickOnLastDeleteButton();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    serverDeleteDialog = new ServerDeleteDialog();
    expect(await serverDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/platformApp.server.delete.question/);
    await serverDeleteDialog.clickOnConfirmButton();

    await serverComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
    expect(await serverComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
