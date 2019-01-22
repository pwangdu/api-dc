/* tslint:disable no-unused-expression */
import { browser, element, by, protractor } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import DatapointComponentsPage from './datapoint.page-object';
import { DatapointDeleteDialog } from './datapoint.page-object';
import DatapointUpdatePage from './datapoint-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('Datapoint e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let datapointUpdatePage: DatapointUpdatePage;
  let datapointComponentsPage: DatapointComponentsPage;
  let datapointDeleteDialog: DatapointDeleteDialog;

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

  it('should load Datapoints', async () => {
    await navBarPage.getEntityPage('datapoint');
    datapointComponentsPage = new DatapointComponentsPage();
    expect(await datapointComponentsPage.getTitle().getText()).to.match(/Datapoints/);
  });

  it('should load create Datapoint page', async () => {
    await datapointComponentsPage.clickOnCreateButton();
    datapointUpdatePage = new DatapointUpdatePage();
    expect(await datapointUpdatePage.getPageTitle().getAttribute('id')).to.match(/platformApp.datapoint.home.createOrEditLabel/);
  });

  it('should create and save Datapoints', async () => {
    const nbButtonsBeforeCreate = await datapointComponentsPage.countDeleteButtons();

    await datapointUpdatePage.setTagInput('tag');
    expect(await datapointUpdatePage.getTagInput()).to.match(/tag/);
    await datapointUpdatePage.setCaptureTimeInput('01/01/2001' + protractor.Key.TAB + '02:30AM');
    expect(await datapointUpdatePage.getCaptureTimeInput()).to.contain('2001-01-01T02:30');
    await datapointUpdatePage.setValueInput('5');
    expect(await datapointUpdatePage.getValueInput()).to.eq('5');
    await datapointUpdatePage.rackSelectLastOption();
    await waitUntilDisplayed(datapointUpdatePage.getSaveButton());
    await datapointUpdatePage.save();
    await waitUntilHidden(datapointUpdatePage.getSaveButton());
    expect(await datapointUpdatePage.getSaveButton().isPresent()).to.be.false;

    await datapointComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
    expect(await datapointComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
  });

  it('should delete last Datapoint', async () => {
    await datapointComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeDelete = await datapointComponentsPage.countDeleteButtons();
    await datapointComponentsPage.clickOnLastDeleteButton();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    datapointDeleteDialog = new DatapointDeleteDialog();
    expect(await datapointDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/platformApp.datapoint.delete.question/);
    await datapointDeleteDialog.clickOnConfirmButton();

    await datapointComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
    expect(await datapointComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
