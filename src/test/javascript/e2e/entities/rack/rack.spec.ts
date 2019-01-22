/* tslint:disable no-unused-expression */
import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import RackComponentsPage from './rack.page-object';
import { RackDeleteDialog } from './rack.page-object';
import RackUpdatePage from './rack-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('Rack e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let rackUpdatePage: RackUpdatePage;
  let rackComponentsPage: RackComponentsPage;
  let rackDeleteDialog: RackDeleteDialog;

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

  it('should load Racks', async () => {
    await navBarPage.getEntityPage('rack');
    rackComponentsPage = new RackComponentsPage();
    expect(await rackComponentsPage.getTitle().getText()).to.match(/Racks/);
  });

  it('should load create Rack page', async () => {
    await rackComponentsPage.clickOnCreateButton();
    rackUpdatePage = new RackUpdatePage();
    expect(await rackUpdatePage.getPageTitle().getAttribute('id')).to.match(/platformApp.rack.home.createOrEditLabel/);
  });

  it('should create and save Racks', async () => {
    const nbButtonsBeforeCreate = await rackComponentsPage.countDeleteButtons();

    await rackUpdatePage.setRackIdInput('rackId');
    expect(await rackUpdatePage.getRackIdInput()).to.match(/rackId/);
    await rackUpdatePage.zoneMonitorSelectLastOption();
    await waitUntilDisplayed(rackUpdatePage.getSaveButton());
    await rackUpdatePage.save();
    await waitUntilHidden(rackUpdatePage.getSaveButton());
    expect(await rackUpdatePage.getSaveButton().isPresent()).to.be.false;

    await rackComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
    expect(await rackComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
  });

  it('should delete last Rack', async () => {
    await rackComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeDelete = await rackComponentsPage.countDeleteButtons();
    await rackComponentsPage.clickOnLastDeleteButton();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    rackDeleteDialog = new RackDeleteDialog();
    expect(await rackDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/platformApp.rack.delete.question/);
    await rackDeleteDialog.clickOnConfirmButton();

    await rackComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
    expect(await rackComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
